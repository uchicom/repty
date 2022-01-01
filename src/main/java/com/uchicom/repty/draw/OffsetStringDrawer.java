package com.uchicom.repty.draw;

import java.awt.Color;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

import com.uchicom.repty.Repty;
import com.uchicom.repty.dto.Draw;
import com.uchicom.repty.dto.Font;
import com.uchicom.repty.dto.Text;
import com.uchicom.repty.dto.Value;
import com.uchicom.repty.util.DrawUtil;

public class OffsetStringDrawer extends AbstractDrawer {

	public OffsetStringDrawer(Repty repty, Draw draw) {
		super(repty,draw);
	}

	@Override
	public void draw(PDPageContentStream stream, Map<String, Object> paramMap) throws IOException {
		Text text = textMap.get(draw.getKey());
		Color recordColor1 = colorMap.get(text.getColorKey());

		Font recordFont1 = fontMap.get(text.getFontKey());
		PDFont recordPdFont1 = repty.pdFontMap.get(text.getFontKey());

		stream.setNonStrokingColor(recordColor1);
		stream.setFont(recordPdFont1, recordFont1.getSize());
		List<?> list = (List<?>) paramMap.get(draw.getList());
		if (list == null || list.isEmpty())
			return;
		int size = list.size() - 1;
		for (int i = 0; i < draw.getValues().size(); i++) {
			Value value = draw.getValues().get(i);
			DrawUtil.drawOffsetString(stream, value, recordPdFont1, recordFont1.getSize(), size);
		}
	}
}
