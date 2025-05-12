package com.board.notice.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.board.notice.dto.request.BoardRequestDTO;
import com.board.notice.dto.response.BoardResponseDTO;
import com.board.notice.entity.Board;
import com.board.notice.entity.User;
import com.board.notice.repository.BoardRepository;
import com.board.notice.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService{
	private final BoardRepository boardRepository;
	private final UserRepository userRepository;
	private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
	
//	게시글 전체조회
	@Override
	public List<BoardResponseDTO> list() {
		List<Board> list = boardRepository.findAll();
		
		return list.stream().map(board -> new BoardResponseDTO(board)).toList();
	}
	
//	게시글 상세보기
	@Override
	@Transactional
	public BoardResponseDTO detail(int bno) {
		Board board = boardRepository.findById(bno).orElseThrow(() -> new EntityNotFoundException("해당 게시글은 존재하지 않습니다."));
		// 조회수 증가 메서드
		board.increaseViewCount();
		// DB 반영
		boardRepository.save(board);
		return new BoardResponseDTO(board);
	}
	
//	게시글 등록하기
	@Override
	@Transactional
	public void register(BoardRequestDTO boardRequestDTO, MultipartFile file) throws IOException {
		String filePath = null;
		
		if(!(file.isEmpty())) {
			// 확장자 추출
			String filename = file.getOriginalFilename();
			String extension = filename.substring(filename.lastIndexOf("."));
			// uuid로 파일명 변경
			String uuid = UUID.randomUUID().toString();
			String newfilename = uuid + extension;
			// (로컬)저장 경로 지정
//			String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/img/upload/";
//			File dest = new File(uploadDir + newfilename);
//			multipartFile.transferTo(dest);
//			filePath = "/img/upload/" + newfilename;
			try(InputStream inputStream = file.getInputStream()) {
				String s3key = "upload/" + newfilename;
				s3Client.putObject(
						PutObjectRequest
							.builder()
							.bucket(bucketName)
							.key(s3key)
							.contentType(file.getContentType())
							.build(),
						RequestBody.fromInputStream(inputStream, file.getSize())
				);
				filePath = "https://" + bucketName + ".s3.amazonaws.com/upload/" + newfilename;
			}
		}
		// 회원 조회
		User user = userRepository.findById(boardRequestDTO.getUserId()).orElseThrow(() -> new UsernameNotFoundException("해당 회원은 존재하지 않습니다."));
		
		// 게시글 등록
		Board board = Board.builder()
				.title(boardRequestDTO.getTitle())
				.content(boardRequestDTO.getContent())
				.category(boardRequestDTO.getCategory())
				.writer(boardRequestDTO.getWriter())
				.filePath(filePath)
				.tags(boardRequestDTO.getTags())
				.userId(user)
				.build();
		boardRepository.save(board);
	}
	
//	게시글 수정하기
	@Override
	@Transactional
	public void update(BoardRequestDTO boardRequestDTO, MultipartFile file) throws IOException {
		// 업로드 파일 변경이 있을 시
		if(boardRequestDTO.isFilechanged() == true) {
			// 변경한 파일이 null이 아닐 경우에만 실행
			if(!(file.isEmpty())){
				// 확장자 추출
				String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
				// uuid로 파일명 변경
				String uuid = UUID.randomUUID().toString();
				String newfilename = uuid + extension;
				try(InputStream is = file.getInputStream()){
					// S3 저장소에 저장
					String s3key = "upload/" + newfilename;
					s3Client.putObject(
							PutObjectRequest
								.builder()
								.bucket(bucketName)
								.key(s3key)
								.contentType(file.getContentType())
								.build(),
							RequestBody.fromInputStream(is, file.getSize())
					);
					boardRequestDTO.setFilePath("https://" + bucketName + ".s3.amazonaws.com/upload/" + newfilename);
				}
			}
		}
		// 수정할 게시글 찾기
		Board board = boardRepository.findById(boardRequestDTO.getBno()).orElseThrow(() -> new EntityNotFoundException("해당 게시글은 존재하지 않습니다."));
		// 게시글 수정 메서드
		board.update(boardRequestDTO);
		// 게시글 DB 반영
		boardRepository.save(board);
	}
	
//	게시글 삭제하기
	@Override
	@Transactional
	public void delete(int bno) {
		Board board = boardRepository.findById(bno).orElseThrow(() -> new EntityNotFoundException("해당 게시글은 존재하지 않습니다."));
		// 게시글 소프트 삭제 메서드
		board.toggleIsDeleted();
		boardRepository.save(board);
	}

}
