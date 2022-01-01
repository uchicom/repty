package com.uchicom.repty.draw;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import com.uchicom.repty.Repty;
import com.uchicom.repty.dto.Draw;
import com.uchicom.repty.dto.Font;
import com.uchicom.repty.dto.Text;
import com.uchicom.repty.util.DrawUtil;

public class RecordStringDrawer extends AbstractDrawer {

	public RecordStringDrawer(Repty repty, Draw draw) {
		super(repty,draw);
	}

	@Override
	public void draw(PDPageContentStream stream, Map<String, Object> paramMap) throws Exception {
		Text recordText = textMap.get(draw.getKey());
		Color recordColor = colorMap.get(recordText.getColorKey());

		Font recordFont = fontMap.get(recordText.getFontKey());
		PDFont recordPdFont = repty.pdFontMap.get(recordText.getFontKey());

		stream.setNonStrokingColor(recordColor);
		stream.setFont(recordPdFont, recordFont.getSize());
		if (draw.getList() != null) {
			List<String> stringList = new ArrayList<>(16);
			DrawUtil.drawRecordString(stream, draw, paramMap, recordPdFont, recordFont.getSize(), stringList);
		}
	}
}
