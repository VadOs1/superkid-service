package com.gmail.dissa.vadim.superkid.repository;

import com.gmail.dissa.vadim.superkid.domain.ProductSize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductSizeRepository extends JpaRepository<ProductSize, Long> {
}
