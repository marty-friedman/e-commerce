/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.commercesearch.searchandizing.sorting.populators;

import static com.google.common.collect.Lists.newArrayList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.commercesearch.model.AbstractSolrSortConditionModel;
import de.hybris.platform.commercesearch.model.ConditionalSolrSortModel;
import de.hybris.platform.commercesearch.model.SelectedCategoryHierarchySolrSortConditionModel;
import de.hybris.platform.commercesearch.search.solrfacetsearch.populators.ConditionalSortFilteringIndexedTypePopulator;
import de.hybris.platform.commercesearch.searchandizing.sorting.SortEvaluatorService;
import de.hybris.platform.commercesearch.searchandizing.sorting.evaluators.IndexedTypeSortEvaluator;
import de.hybris.platform.commercesearch.searchandizing.sorting.evaluators.impl.SelectedCategoryHierarchyIndexedTypeSortEvaluator;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.commerceservices.search.pagedata.SortData;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchRequest;
import de.hybris.platform.commerceservices.search.solrfacetsearch.data.SolrSearchResponse;
import de.hybris.platform.solrfacetsearch.config.IndexedType;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import de.hybris.platform.solrfacetsearch.config.IndexedTypeSort;
import de.hybris.platform.solrfacetsearch.model.SolrSortModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Maps;


public class ConditionalSortFilteringIndexedTypePopulatorTest
{
    ConditionalSortFilteringIndexedTypePopulator populator = new ConditionalSortFilteringIndexedTypePopulator();

    private static final String INDEX_TYPE_SORT_CODE_VISIBLE = "visibleCode";
    private static final String INDEX_TYPE_SORT_CODE_INVISIBLE = "invisibleCode";

    @Mock
    SortEvaluatorService sortEvaluatorService;

    @Mock
    SolrSortModel visibleSort;

    @Mock
    SolrSortModel invisibleSort;

    @Mock
    ConditionalSolrSortModel condSort;

    SolrSearchResponse source = new SolrSearchResponse();
    SearchPageData target = new SearchPageData();
    SortData visibleSortData;
    SortData invisibleSortData;

    @Before
    public void init()
    {
        MockitoAnnotations.initMocks(this);

        given(visibleSort.getVisible()).willReturn(Boolean.TRUE);
        given(invisibleSort.getVisible()).willReturn(Boolean.FALSE);

        populator.setSortEvaluatorService(sortEvaluatorService);

        final SolrSearchRequest request = new SolrSearchRequest();
        request.setIndexedType(buildIndexType());

        source.setRequest(request);

        visibleSortData = buildSortData(INDEX_TYPE_SORT_CODE_VISIBLE);
        invisibleSortData = buildSortData(INDEX_TYPE_SORT_CODE_INVISIBLE);
    }

    @Test
    public void shouldFilterInvisibleSorts()
    {
        //given
        populator.setObserveVisible(true);

        final List<SortData> inputSorts = Arrays.asList(visibleSortData, invisibleSortData);
        target.setSorts(inputSorts);
        //when
        populator.populate(source, target);

        //then
        assertThat(target.getSorts()).containsOnly(visibleSortData);
    }

    @Test
    public void shouldNotFilterInvisibleSortsIfFlagSetToFalse()
    {
        //given
        populator.setObserveVisible(false);

        final List<SortData> inputSorts = Arrays.asList(visibleSortData, invisibleSortData);
        target.setSorts(inputSorts);
        //when
        populator.populate(source, target);

        //then
        assertThat(target.getSorts()).containsOnly(visibleSortData, invisibleSortData);
    }

    @Test
    public void shouldFilterConditionalSorts()
    {
        //given
        populator.setObserveVisible(false);
        final IndexedType indexedType = (IndexedType) source.getRequest().getIndexedType();

        final IndexedTypeSort invisibleIndexedTypeSort = indexedType.getSortsByCode().get(INDEX_TYPE_SORT_CODE_INVISIBLE);
        final IndexedTypeSort visibleIndexedTypeSort = indexedType.getSortsByCode().get(INDEX_TYPE_SORT_CODE_VISIBLE);
        visibleIndexedTypeSort.setSort(condSort);

        final List<SortData> inputSorts = Arrays.asList(visibleSortData, invisibleSortData);
        target.setSorts(inputSorts);

        final LinkedHashMap<AbstractSolrSortConditionModel, IndexedTypeSortEvaluator> values = Maps.newLinkedHashMap();
        final SelectedCategoryHierarchySolrSortConditionModel condition = mock(SelectedCategoryHierarchySolrSortConditionModel.class);
        given(condition.getInverse()).willReturn(Boolean.FALSE);
        final IndexedTypeSortEvaluator evaluator = mock(SelectedCategoryHierarchyIndexedTypeSortEvaluator.class);

        given(Boolean.valueOf(evaluator.evaluateFilter(source.getRequest(), visibleIndexedTypeSort, condition))).willReturn(Boolean.TRUE);
        given(Boolean.valueOf(evaluator.evaluateFilter(source.getRequest(), invisibleIndexedTypeSort, condition))).willReturn(Boolean.FALSE);

        values.put(condition, evaluator);
        given(sortEvaluatorService.getEvaluatorsForConditionalSort(condSort)).willReturn(values);

        //when
        populator.populate(source, target);

        //then
        assertThat(target.getSorts()).containsOnly(invisibleSortData);
    }

    @Test
    public void shouldFilterConditionalSortsWithInverse()
    {
        //given
        populator.setObserveVisible(false);

        final IndexedType indexedType = (IndexedType) source.getRequest().getIndexedType();

        final IndexedTypeSort invisibleIndexedTypeSort = indexedType.getSortsByCode().get(INDEX_TYPE_SORT_CODE_INVISIBLE);
        final IndexedTypeSort visibleIndexedTypeSort = indexedType.getSortsByCode().get(INDEX_TYPE_SORT_CODE_VISIBLE);
        visibleIndexedTypeSort.setSort(condSort);

        final List<SortData> inputSorts = Arrays.asList(visibleSortData, invisibleSortData);
        target.setSorts(inputSorts);

        final LinkedHashMap<AbstractSolrSortConditionModel, IndexedTypeSortEvaluator> values = Maps.newLinkedHashMap();
        final SelectedCategoryHierarchySolrSortConditionModel condition = mock(SelectedCategoryHierarchySolrSortConditionModel.class);
        given(condition.getInverse()).willReturn(Boolean.TRUE);

        final IndexedTypeSortEvaluator evaluator = mock(SelectedCategoryHierarchyIndexedTypeSortEvaluator.class);
        given(Boolean.valueOf(evaluator.evaluateFilter(source.getRequest(), visibleIndexedTypeSort, condition))).willReturn(Boolean.TRUE);
        given(Boolean.valueOf(evaluator.evaluateFilter(source.getRequest(), invisibleIndexedTypeSort, condition))).willReturn(Boolean.FALSE);

        values.put(condition, evaluator);
        given(sortEvaluatorService.getEvaluatorsForConditionalSort(condSort)).willReturn(values);

        //when
        populator.populate(source, target);

        //then
        assertThat(target.getSorts()).containsOnly(visibleSortData, invisibleSortData);
    }

    private IndexedType buildIndexType(){
        final IndexedType indexedType = new IndexedType();

        final IndexedTypeSort sortVisible = new IndexedTypeSort();
        sortVisible.setCode(INDEX_TYPE_SORT_CODE_VISIBLE);
        sortVisible.setSort(visibleSort);
        final IndexedTypeSort sortInvisible = new IndexedTypeSort();
        sortInvisible.setCode(INDEX_TYPE_SORT_CODE_INVISIBLE);
        sortInvisible.setSort(invisibleSort);

        final List<IndexedTypeSort> sorts = newArrayList(sortVisible, sortInvisible);
        indexedType.setSorts(sorts);
        indexedType.setSortsByCode(sorts.stream().collect(Collectors.toMap(s -> s.getCode(), s -> s)));

        return indexedType;
    }

    private SortData buildSortData(final String indexTypeCode){
        final SortData sortData = new SortData();
        sortData.setCode(indexTypeCode);

        return sortData;
    }
}
