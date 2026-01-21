package com.dw.scheduler.repository;

import com.dw.scheduler.entity.Directory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DirectoryRepository extends JpaRepository<Directory, Long>, JpaSpecificationExecutor<Directory> {

    /**
     * 根据父目录ID查询子目录列表
     */
    List<Directory> findByParentIdOrderByCreateTimeAsc(Long parentId);

    /**
     * 根据目录名称和父目录ID查询
     */
    Directory findByDirectoryNameAndParentId(String directoryName, Long parentId);
}
