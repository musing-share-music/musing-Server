
package com.example.musing.repository.Board;
import com.example.musing.entity.Board.BCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BCategoryRepository extends JpaRepository<BCategory, Long> {


    // 조건없이 테이블의 전체 레코드 조회
    // 실제 서비스에서는 잘 사용되지 않음
    @Override
    List<BCategory> findAll();

    // 엔티티를 리스트 형식으로 받아 테이블에 한번에 저장
    @Override
    <S extends BCategory> List<S> saveAll(Iterable<S> entities);

    // 현재 JPA Context 가지고 있는 값을 DB에 반영
    void flush();

    // 엔티티를 리스트 형식으로 받아 레코드를 한번에 삭제
    @Deprecated
    default void deleteInBatch(Iterable<BCategory> entities) { deleteAllInBatch(entities); }

    // 조건없이 테이블의 전체 레코드 삭제
    void deleteAllInBatch();


}