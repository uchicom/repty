package com.uchicom.repty.draw;

import java.util.Map;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

import com.uchicom.repty.Repty;
import com.uchicom.repty.dto.Draw;

public class FormDrawer extends AbstractDrawer {

	public FormDrawer(Repty repty, Draw draw) {
		super(repty,draw);
	}

	@Override
	public void draw(PDPageContentStream stream, Map<String, Object> paramMap) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
