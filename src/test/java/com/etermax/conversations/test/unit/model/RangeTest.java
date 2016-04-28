package com.etermax.conversations.test.unit.model;

import com.etermax.conversations.error.InvalidRangeException;
import com.etermax.conversations.model.Range;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RangeTest {
	@Test
	public void testEmptyRange() throws InvalidRangeException {
		//Given

		//When
		Range range = new Range(null, null);

		//Then
		assertThat(range.isInRange(3l)).isEqualTo(true);
	}

	@Test
	public void testRange() throws InvalidRangeException {
		//Given

		//When
		Range range = new Range(1l, 4l);

		//Then
		assertThat(range.isInRange(4l)).isEqualTo(true);
		assertThat(range.isInRange(5l)).isEqualTo(false);
		assertThat(range.getFirstDate()).isEqualTo(1l);
		assertThat(range.getLastDate()).isEqualTo(4l);
	}
}
