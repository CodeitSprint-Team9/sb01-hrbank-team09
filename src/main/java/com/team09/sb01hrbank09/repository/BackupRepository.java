package com.team09.sb01hrbank09.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team09.sb01hrbank09.entity.Backup;

@Repository
public interface BackupRepository extends JpaRepository<Backup, Long> {

	Backup save(Backup backup);

	Optional<Backup> findById(Long id);

	List<Backup> findAll();

	void deleteById(Long id);

}
