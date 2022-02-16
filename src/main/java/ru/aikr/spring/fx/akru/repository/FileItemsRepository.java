package ru.aikr.spring.fx.akru.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.aikr.spring.fx.akru.domain.FileItemEntity;

public interface FileItemsRepository extends JpaRepository<FileItemEntity, Long>{
}