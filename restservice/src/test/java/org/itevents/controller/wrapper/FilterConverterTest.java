package org.itevents.controller.wrapper;

import org.itevents.controller.AbstractControllerTest;
import org.itevents.controller.EventRestController;
import org.itevents.controller.converter.FilterConverter;
import org.itevents.dao.model.City;
import org.itevents.dao.model.Filter;
import org.itevents.dao.model.Technology;
import org.itevents.service.CityService;
import org.itevents.service.TechnologyService;
import org.itevents.test_utils.BuilderUtil;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:converters.xml", "classpath:applicationContext.xml"})
public class FilterConverterTest{

    @Mock
    private TechnologyService technologyService;

    @Mock
    private CityService cityService;

    @InjectMocks
    @Inject
    private FilterConverter filterConverter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Ignore
    @Test
    public void shouldReturnFilterWithFreeJavaEventsInKuivAtWeek() throws ParseException {
        FilterWrapper filteWrapperWithFreeJavaEventsInKuivAtWeek
                = BuilderUtil.buildFilteWrapperWithFreeJavaEventsInKuivAtWeek();
        Filter expectedfilterWithFreeJavaEventsInKuivAtWeek
                = BuilderUtil.buildFilterWithFreeJavaEventsInKuivAtWeek();
        String[] technologyNames = {"Java"};
        Technology javaTechnology = BuilderUtil.buildTechnologyJava();
        City kyivCity = BuilderUtil.buildCityKyiv();

        when(technologyService.getTechnologiesByNames(technologyNames))
                .thenReturn(new ArrayList<>(Arrays.asList(javaTechnology)));
        when(cityService.getCity(kyivCity.getId()))
                .thenReturn(kyivCity);

        Filter returnedFilter = filterConverter.toFilter(filteWrapperWithFreeJavaEventsInKuivAtWeek);

        verify(technologyService, times(1)).getTechnologiesByNames(technologyNames);
        verify(cityService, times(1)).getCity(kyivCity.getId());
        assertEquals(expectedfilterWithFreeJavaEventsInKuivAtWeek, returnedFilter);

    }
}
