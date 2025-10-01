package com.board.notice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.board.notice.dto.request.DwellTimeRequestDTO;
import com.board.notice.service.DwellTimeService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/log")
public class DwellTimeRestController {
	private final DwellTimeService dwellTimeService;

	@PostMapping("/dwell-time")
	public ResponseEntity<?> logDwellTime(@RequestBody DwellTimeRequestDTO dto, HttpServletRequest request) {

		dwellTimeService.saveDwellTime(dto);
		return ResponseEntity.ok("체류 시간 기록 완료");
	}
}
