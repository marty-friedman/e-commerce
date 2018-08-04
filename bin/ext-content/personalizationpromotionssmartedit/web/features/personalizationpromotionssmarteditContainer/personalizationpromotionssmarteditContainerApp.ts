import * as angular from 'angular';
import './requireLegacyJsFiles';

angular.module('personalizationpromotionssmarteditContainer', [
	'personalizationsmarteditPromotionModule',
	'personalizationpromotionssmarteditContainerTemplates',
	'yjqueryModule'
])
	.run((
		yjQuery: any,
		domain: any) => {
		'ngInject';

		const loadCSS = (href: string) => {
			const cssLink = yjQuery("<link rel='stylesheet' type='text/css' href='" + href + "'>");
			yjQuery("head").append(cssLink);
		};
		loadCSS(domain + "/personalizationpromotionssmartedit/css/style.css");

	});
