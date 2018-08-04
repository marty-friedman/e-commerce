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
/**
 * @ngdoc overview
 * @name seValidationMessageParserModule
 * @description
 * This module provides the seValidationMessageParser service, which is used to parse validation messages (errors, warnings)
 * for parameters such as language and format, which are sent as part of the message itself.
 */
angular.module('seValidationMessageParserModule', [])
    /**
     * @ngdoc service
     * @name seValidationMessageParserModule.seValidationMessageParser
     * @description
     * This service provides the functionality to parse validation messages (errors, warnings) received from the backend.
     */
    .service('seValidationMessageParser', function() {

        /**
         * @ngdoc method
         * @name seValidationMessageParserModule.seValidationMessageParser.parse
         * @methodOf seValidationMessageParserModule.seValidationMessageParser
         * @description
         * Parses extra details, such as language and format, from a validation message (error, warning). These details are also
         * stripped out of the final message. This function expects the message to be in the following format:
         *
         * <pre>
         * var message = "Some validation message occurred. Language: [en]. Format: [widescreen]. SomeKey: [SomeVal]."
         * </pre>
         *
         * The resulting message object is as follows:
         * <pre>
         * {
         *     message: "Some validation message occurred."
         *     language: "en",
         *     format: "widescreen",
         *     somekey: "someval"
         * }
         * </pre>
         */
        this.parse = function(message) {
            var expression = new RegExp('[a-zA-Z]+: (\[|\{)([a-zA-Z0-9]+)(\]|\})\.?', 'g');
            var matches = message.match(expression) || [];
            return matches.reduce(function(messages, match) {
                messages.message = messages.message.replace(match, '').trim();
                var key = match.split(':')[0].trim().toLowerCase();
                var value = match.split(':')[1].match(/[a-zA-Z0-9]+/g)[0];

                messages[key] = value;
                return messages;
            }, {
                message: message
            });
        };
    });
