package com.soarclient.utils;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;

public class SearchUtils {

	private static final LevenshteinDistance LEVENSHTEIN_DISTANCE = LevenshteinDistance.getDefaultInstance();

	public static boolean isSimilar(String s1, String s2) {
		return isSimilar(s1, s2, 2);
	}

	public static boolean isSimilar(String s1, String s2, int searchDistance) {

		s1 = s1.toLowerCase(Locale.ENGLISH);
		s2 = s2.toLowerCase(Locale.ENGLISH);

		if (s1.length() <= searchDistance) {
			return s1.contains(s2);
		}

		for (String word : StringUtils.split(s1)) {
			if (word.contains(s2) || LEVENSHTEIN_DISTANCE.apply(word, s2) <= searchDistance) {
				return true;
			}
		}

		return s1.contains(s2) || LEVENSHTEIN_DISTANCE.apply(s1, s2) <= searchDistance;
	}
}