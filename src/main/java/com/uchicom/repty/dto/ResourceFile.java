// (c) 2018 uchicom
package com.uchicom.repty.dto;

/**
 * リソースファイル情報.
 * @author shigeki.uchiyama
 *
 */
public class ResourceFile {

	private String file;
	private boolean resource;
	
	public ResourceFile(String file) {
		this.file = file;
	}
	public ResourceFile(String file, boolean resource) {
		this.file = file;
		this.resource = resource;
	}
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public boolean isResource() {
		return resource;
	}
	public void setResource(boolean resource) {
		this.resource = resource;
	}
}
