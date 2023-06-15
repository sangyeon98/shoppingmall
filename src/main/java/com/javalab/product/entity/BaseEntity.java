package com.javalab.product.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * 베이스 엔티티 클래스
 * - `@EntityListeners`로 지정된 엔티티 리스너가 감사 목적으로 등록 
 *    및 호출되도록 합니다.
   - 또한 `createdBy`, `createdDate`, `lastModifiedBy` 및 
      `lastModifiedDate`와 같은 엔티티의 감사 관련 필드를 자동으로 
      채우도록 필요한 인프라를 구성합니다.
   - 등록일자/수정일자 필드를 엔티티가 생성될 때 감시하고 있다고
     자동으로 값을 세팅해준다.   
 */
@MappedSuperclass
@EntityListeners(value = { AuditingEntityListener.class })
@Getter
public abstract class BaseEntity {

    @CreatedDate
    @Column(name = "regdate", updatable = false)
    private LocalDateTime regDate;

    @LastModifiedDate
    @Column(name ="moddate" )
    private LocalDateTime modDate;

}
