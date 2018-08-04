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
import * as angular from 'angular';
import {annotationService} from 'smarteditcommons';

/*
 * see test fucntions and classed at the end of the test suite
 */

describe('test annotationService', function() {

	//////////////////////////////////////////////
	///////// TEST FUNCTION AND CLASSES //////////
	//////////////////////////////////////////////

	const annotation1 = annotationService.getMethodAnnotationFactory('annotation1');

	function annotation1Factory($dep1: angular.ILogService) {
		return annotationService.setMethodAnnotationFactory("annotation1", (factoryArgument: any[]) => {

			return function(target: any, propertyName: string, originalMethod: (...x: any[]) => any, ...invocationArguments: any[]) {
				$dep1.info(`will wrap in annotation1`);
				return "wrappedInAnnotation1_" + originalMethod.apply(this, invocationArguments);
			};
		});
	}

	const annotation2 = annotationService.getMethodAnnotationFactory('annotation2');

	function annotation2Factory($dep2: angular.ILogService) {
		return annotationService.setMethodAnnotationFactory("annotation2", (factoryArgument: any[]) => {

			return function(target: any, propertyName: string, originalMethod: (...x: any[]) => any, ...invocationArguments: any[]) {
				$dep2.info(`will wrap in annotation2 with factory arguments ${factoryArgument[0]} and ${factoryArgument[1]}`);
				return "wrappedInAnnotation2_" + originalMethod.apply(this, invocationArguments);
			};
		});
	}

	class ServiceToBeAnnotated {

		@annotation1()
		@annotation2('Frequent', 3)
		someMethod1(): string {
			return "rawMethod1Output";
		}

		@annotation2('Never', 5)
		@annotation1()
		someMethod2(): string {
			return "rawMethod2Output";
		}

	}

	//////////////////////////////////////////////
	//////////////// ACTUAL TESTS ////////////////
	//////////////////////////////////////////////

	const service = new ServiceToBeAnnotated();
	let $log: jasmine.SpyObj<angular.ILogService>;

	beforeEach(() => {
		$log = jasmine.createSpyObj<angular.ILogService>('$log', ['info']);
	});

	function initializeBothAnnotations() {
		// invocations normally performed when the pertaining angular factories are called
		annotation1Factory($log);
		annotation2Factory($log);
	}

	function onlyInitialize1Annotation1() {
		// invocations normally performed when the pertaining angular factories are called
		annotation1Factory($log);
	}

	xit('GIVEN a same annotation is registered twice THEN an exception is thrown', () => {
		annotationService.getMethodAnnotationFactory("someannotation");

		expect(function() {
			annotationService.getMethodAnnotationFactory("someannotation");
		}).toThrow(new Error("annotation 'cache' has already been registered"));
	});

	it('GIVEN one necessary annotation is not initialized WHEN method1 is called THEN exception is thrown', () => {
		onlyInitialize1Annotation1();

		expect(function() {
			service.someMethod1();
		}).toThrow(new Error("annotation 'annotation2' is used but its MethodAnnotationFactory may not have been added to the dependency injection"));
	});

	it('WHEN method1 is called THEN the method1 is proxied twice: first cache then transactional', () => {
		initializeBothAnnotations();

		expect(service.someMethod1()).toBe("wrappedInAnnotation1_wrappedInAnnotation2_rawMethod1Output");
	});

	it('WHEN method2 is called THEN the method2 is proxied twice: first transactional then cache', () => {
		initializeBothAnnotations();

		expect(service.someMethod2()).toBe("wrappedInAnnotation2_wrappedInAnnotation1_rawMethod2Output");
	});

	it('WHEN method1 is called THEN $log.info is called twice', () => {
		initializeBothAnnotations();

		service.someMethod1();

		expect($log.info.calls.count()).toBe(2);
		expect($log.info).toHaveBeenCalledWith("will wrap in annotation1");
		expect($log.info).toHaveBeenCalledWith("will wrap in annotation2 with factory arguments Frequent and 3");
	});

});


