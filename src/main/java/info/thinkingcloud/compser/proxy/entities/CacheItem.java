package info.thinkingcloud.compser.proxy.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.hibernate.annotations.Index;

@Entity
public class CacheItem implements Serializable {

	private static final long serialVersionUID = 5450250258052115867L;

	public CacheItem() {

	}

	public CacheItem(String cache, String name, String value) {
		this.cache = cache;
		this.name = name;
		this.value = value;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(length = 16, nullable = false)
	@Index(name = "cache", columnNames = { "cache", "name" })
	private String cache;

	@Column(length = 64, nullable = false)
	@Index(name = "name")
	private String name;

	@Column(columnDefinition = "clob")
	@Lob
	private String value;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCache() {
		return cache;
	}

	public void setCache(String cache) {
		this.cache = cache;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
