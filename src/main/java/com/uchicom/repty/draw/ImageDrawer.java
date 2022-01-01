package com.uchicom.repty.draw;

import java.io.IOException;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import com.uchicom.repty.Repty;
import com.uchicom.repty.dto.Draw;
import com.uchicom.repty.dto.Value;

public class ImageDrawer extends AbstractDrawer {

	public ImageDrawer(Repty repty, Draw draw) {
		super(repty,draw);
	}

	@Override
	public void draw(PDPageContentStream stream, Map<String, Object> paramMap) throws IOException {
		PDImageXObject imagex = repty.xImageMap.get(draw.getKey());
		for (Value value : draw.getValues()) {
			if (value.getX1() == value.getX2()) {
				stream.drawImage(imagex, value.getX1(), value.getY1());
			} else {
				stream.drawImage(imagex, value.getX1(), value.getY1(), value.getX2() - value.getX1(),
						value.getY2() - value.getY1());
			}
		}
	}
}
