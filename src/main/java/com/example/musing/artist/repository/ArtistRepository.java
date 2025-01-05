
package com.example.musing.artist.repository;

import com.example.musing.artist.entity.Artist;
import com.example.musing.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;


public interface ArtistRepository extends JpaRepository<Artist, Long>, JpaSpecificationExecutor<Board> {

    Optional<Artist> findByName(String name);


}