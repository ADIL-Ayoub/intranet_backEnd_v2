package com.procheck.intranet.models;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@EntityListeners(AuditingEntityListener.class)
@Setter
public class BaseEntity {

	@Id
	@GeneratedValue(generator = "uuid2")
	@Column(name = "ID", unique = true, nullable = false)
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	private UUID id;

	@Version
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "VERSION")
	private Date version;
}
