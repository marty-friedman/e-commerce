/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
import 'jasmine';
import {TextTruncateService} from "smarteditcommons/services/text/textTruncateService";
import {TruncatedText} from "smarteditcommons/dtos/TruncatedText";

describe('TextTruncateService - ', () => {

	let textTruncateService: TextTruncateService;
	const TEXT = 'The quick brown fox jumps over the lazy dog';
	const TEXT_TRUNCATED = 'The quick brown';

	beforeEach(() => {

		const fixture = AngularUnitTestHelper.prepareModule('yMoreTextModule')
			.service('textTruncateService');

		textTruncateService = fixture.service;
	});

	it('truncateToNearestWord should return truncated text', () => {

		// GIVEN
		const NUM_CHARACTERS = 18;

		// WHEN
		const returnedText: TruncatedText = textTruncateService.truncateToNearestWord(NUM_CHARACTERS, TEXT);

		// THEN
		expect(returnedText.getTruncatedText()).toEqual(TEXT_TRUNCATED);
	});

	it('truncateToNearestWord should return untruncated text if number of characters are larger than text', () => {

		// GIVEN
		const NUM_CHARACTERS = 500;

		// WHEN
		const returnedText: TruncatedText = textTruncateService.truncateToNearestWord(NUM_CHARACTERS, TEXT);

		// THEN
		expect(returnedText.getTruncatedText()).toEqual(TEXT);
	});

	it('truncateToNearestWord should return no text if number of characters is 0', () => {

		// GIVEN
		const NUM_CHARACTERS = 0;

		// WHEN
		const returnedText: TruncatedText = textTruncateService.truncateToNearestWord(NUM_CHARACTERS, TEXT);

		// THEN
		expect(returnedText.getTruncatedText()).toEqual('');
	});

	it('truncateToNearestWord should should return TruncatedText with truncated set to true', () => {
		// GIVEN
		const NUM_CHARACTERS = 5;

		// WHEN
		const returnedText: TruncatedText = textTruncateService.truncateToNearestWord(NUM_CHARACTERS, TEXT);

		// THEN
		expect(returnedText.isTruncated()).toEqual(true);
	});

	it('truncateToNearestWord should should return TruncatedText with truncated set to false', () => {
		// GIVEN
		const NUM_CHARACTERS = 500;

		// WHEN
		const returnedText: TruncatedText = textTruncateService.truncateToNearestWord(NUM_CHARACTERS, TEXT);

		// THEN
		expect(returnedText.isTruncated()).toEqual(false);
	});
});
