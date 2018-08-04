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
import {TypedMap} from 'smarteditcommons';

export type MethodAnnotation = (target: any, propertyName: string, originalMethod: (...x: any[]) => any, ...invocationArguments: any[]) => any;
export type MethodAnnotationFactory = (...x: any[]) => MethodAnnotation;

export class AnnotationService {

	private annotationFactoryMap = {} as TypedMap<MethodAnnotationFactory>;

	setMethodAnnotationFactory(name: string, annotationFactory: MethodAnnotationFactory): MethodAnnotationFactory {
		this.annotationFactoryMap[name] = annotationFactory;
		return annotationFactory;
	}

	getMethodAnnotationFactory(name: string) {
		const instance = this;

		// if (getExternalNameSpace()[name]){
		// 	throw new Error(`annotation '${name}' has already been registered`);
		// }

		return addAsExternal(name, function(...factoryArgument: any[]) {

			return (target: any, propertyName: string, descriptor: TypedPropertyDescriptor<(...x: any[]) => any>) => {

				const originalMethod = descriptor.value;

				descriptor.value = function() {

					const annotationFactory = instance.annotationFactoryMap[name];

					if (annotationFactory) {
						return annotationFactory(factoryArgument)(target, propertyName, originalMethod.bind(this), arguments);
					} else {
						throw new Error(`annotation '${name}' is used but its MethodAnnotationFactory may not have been added to the dependency injection`);
					}
				};
			};
		});
	}
}

export const annotationService: AnnotationService = new AnnotationService();

/**
 * exposes to the smarteditcommons namespace that is declared as "external" in downstream extensions
 */
export function addAsExternal<T>(annotationName: string, annotationFunction: T): T {
	getExternalNameSpace()[annotationName] = annotationFunction;
	return annotationFunction;
}

function getExternalNameSpace(): any {
	(window as any).smarteditcommons = (window as any).smarteditcommons || {};
	return (window as any).smarteditcommons;
}
