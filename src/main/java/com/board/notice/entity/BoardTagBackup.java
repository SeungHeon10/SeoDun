package com.board.notice.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "board_tags_deleted")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardTagBackup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int backupId;

    @Column(name = "board_bno")
    private Integer boardBno;

    @Column(name = "tag")
    private String tag;
    
}
