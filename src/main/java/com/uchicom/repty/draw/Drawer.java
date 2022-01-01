package com.uchicom.repty.draw;

import java.util.Map;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

import com.uchicom.repty.dto.Draw;

public interface Drawer {

	void draw(PDPageContentStream stream, Map<String, Object> paramMap) throws Exception;
	Draw getDraw();
}
