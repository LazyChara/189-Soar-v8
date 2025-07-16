package com.soarclient.libraries.material3;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.soarclient.libraries.material3.dynamiccolor.DynamicScheme;
import com.soarclient.libraries.material3.hct.Hct;
import com.soarclient.libraries.material3.quantize.QuantizerCelebi;
import com.soarclient.libraries.material3.scheme.SchemeTonalSpot;
import com.soarclient.libraries.material3.score.Score;
import com.soarclient.utils.ImageUtils;

public class Material3 {

	public static DynamicScheme getDynamicScheme(Hct hct, boolean dark, double contrast) {
		return new SchemeTonalSpot(hct, dark, contrast);
	}

	public static Hct getImageHct(BufferedImage image) {

		if (image == null) {
			return Hct.from(0, 0, 0);
		}

		Map<Integer, Integer> quantizerResult = QuantizerCelebi.quantize(ImageUtils.imageToPixels(image), 128);
		List<Integer> colors = Score.score(quantizerResult);

		return Hct.fromInt(colors.get(0));
	}

	public static Hct getImageHct(File imageFile) {

		try {
			return getImageHct(ImageIO.read(imageFile));
		} catch (IOException e) {
		}

		return Hct.from(0, 0, 0);
	}
}
