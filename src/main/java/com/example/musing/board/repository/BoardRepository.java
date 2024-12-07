
package com.example.musing.board.repository;
import com.example.musing.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface BoardRepository extends JpaRepository<Board, Long> {

    List<Board> findByTitleContaining(String keyword);

    // 작성자로 게시물 검색
    List<Board> findByAuthor(String author);

    // 특정 작성자가 작성한 게시물을 최신순으로 가져오기
    List<Board> findByAuthorOrderByCreatedAtDesc(String author);

    // JPQL을 사용하여 특정 조건의 게시물 검색 (해당 쿼리는 Board 자체를 가져와서 엔티티에 매핑해야해서 *이 아닌 b를 가져옴) 
    @Query("SELECT b FROM Board b WHERE b.title LIKE %:keyword% OR b.content LIKE %:keyword%")
    List<Board> searchByTitleOrContent(@Param("keyword") String keyword);



}