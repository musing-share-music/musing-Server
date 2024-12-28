
package com.example.musing.board.repository;

import com.example.musing.board.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface BoardRepository extends JpaRepository<Board, Long>, JpaSpecificationExecutor<Board> {
    // 레파지토리 문법이 너무 길어져 가독성이 낮은 부분을 Specification으로 사용
    Page<Board> findByUser_Id(Specification<Board> spec, Pageable pageable, String id);

}