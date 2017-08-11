package org.openmrs.mobile.test.presenters;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.openmrs.mobile.activities.addeditvisit.AddEditVisitContract;
import org.openmrs.mobile.activities.addeditvisit.AddEditVisitPresenter;
import org.openmrs.mobile.application.OpenMRS;
import org.openmrs.mobile.data.DataService;
import org.openmrs.mobile.data.PagingInfo;
import org.openmrs.mobile.data.QueryOptions;
import org.openmrs.mobile.data.impl.ConceptAnswerDataService;
import org.openmrs.mobile.data.impl.LocationDataService;
import org.openmrs.mobile.data.impl.PatientDataService;
import org.openmrs.mobile.data.impl.VisitAttributeTypeDataService;
import org.openmrs.mobile.data.impl.VisitDataService;
import org.openmrs.mobile.data.impl.VisitTypeDataService;
import org.openmrs.mobile.models.Location;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Person;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.models.VisitType;
import org.openmrs.mobile.test.ACUnitTestBase;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@PrepareForTest({OpenMRS.class})
public class AddEditVisitPresenterTest extends ACUnitTestBase {

    @Mock
    private VisitDataService visitDataService;
    @Mock
    private VisitTypeDataService visitTypeDataService;
    @Mock
    private VisitAttributeTypeDataService visitAttributeTypeDataService;
    @Mock
    private PatientDataService patientDataService;
    @Mock
    private ConceptAnswerDataService conceptAnswerDataService;
    @Mock
    private LocationDataService locationDataService;
    @Mock
    private OpenMRS openMRS;

    @Mock
    private AddEditVisitContract.View view;
    private AddEditVisitPresenter presenter;
    private String patientUuid = "11-22-33-44", visitUuid = "24-65-9";
    private Patient patient;
    private Visit visit;
    private Location location;
    private List<Visit> visits = new ArrayList<>();
    private List<VisitType> visitTypes = new ArrayList<>();

    @Before
    public void setUp(){
        presenter = new AddEditVisitPresenter(view, patientUuid, null, false, visitDataService,
                patientDataService, visitTypeDataService, visitAttributeTypeDataService,
                conceptAnswerDataService, locationDataService);

        patient = new Patient();
        patient.setUuid(patientUuid);
        patient.setId(1L);
        Person person = new Person();
        person.setUuid("654321");
        person.setGender("Female");
        person.setBirthdate("2000-02-21");
        patient.setPerson(person);

        location = new Location();
        location.setName("Ward");
        location.setUuid("location-123");
        Location parentLocation = new Location();
        parentLocation.setName("Kijabe");
        parentLocation.setUuid("kijabe-123");
        location.setParentLocation(parentLocation);

        visit = new Visit();
        visit.setStartDatetime(new Date(2017, 5, 1));
        visit.setPatient(patient);
        visit.setVisitType(new VisitType("Inpatient Kijabe", "547874"));
        visit.setLocation(location);
        visits.add(visit);

        visitTypes.add(new VisitType("Inpatient Kijabe", "547874"));

        PowerMockito.mockStatic(OpenMRS.class);
        PowerMockito.when(OpenMRS.getInstance()).thenReturn(openMRS);
        openMRS.getCurrentLoggedInUserInfo().put(ApplicationConstants.UserKeys.USER_UUID, "654321");
        openMRS.setLocation(location.getUuid());

        mockCallbacks();
    }

    private void mockCallbacks(){
        // load patient callback
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                ((DataService.GetCallback<Patient>) invocation.getArguments()[2]).onCompleted(patient);
                return null;
            }
        }).when(patientDataService).getByUuid(anyString(), any(QueryOptions.class), any(DataService.GetCallback.class));

        // load visit callback
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                ((DataService.GetCallback<Visit>) invocation.getArguments()[2]).onCompleted(visit);
                return null;
            }
        }).when(visitDataService).getByUuid(anyString(), any(QueryOptions.class), any(DataService.GetCallback.class));

        // load visit types callback
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                ((DataService.GetCallback<List<VisitType>>) invocation.getArguments()[2]).onCompleted(visitTypes);
                return null;
            }
        }).when(visitTypeDataService).getAll(any(QueryOptions.class), any(PagingInfo.class),
                any(DataService.GetCallback.class));

        // load location callback
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                ((DataService.GetCallback<Location>) invocation.getArguments()[2]).onCompleted(location);
                return null;
            }
        }).when(locationDataService).getByUuid(anyString(), any(QueryOptions.class), any(DataService.GetCallback.class));

        // create visit callback
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                visit.setUuid(visitUuid);
                ((DataService.GetCallback<Visit>) invocation.getArguments()[1]).onCompleted(visit);
                return null;
            }
        }).when(visitDataService).create(any(Visit.class), any(DataService.GetCallback.class));


        // update visit callback
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                ((DataService.GetCallback<Visit>) invocation.getArguments()[1]).onCompleted(visit);
                return null;
            }
        }).when(visitDataService).update(any(Visit.class), any(DataService.GetCallback.class));

        // end visit callback
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                ((DataService.GetCallback<Visit>) invocation.getArguments()[2]).onCompleted(visit);
                return null;
            }
        }).when(visitDataService).endVisit(anyString(), any(Visit.class), any(QueryOptions.class),
                any(DataService.GetCallback.class));

    }

    @Test
    public void shouldInitializePage() throws Exception {
        presenter.subscribe();
        verify(view).initView(false);
        verify(view).updateVisitTypes(visitTypes);
    }

    @Test
    public void shouldStartVisit(){
        presenter.startVisit(new ArrayList<>());
        verify(view).setSpinnerVisibility(false);
        verify(view).showVisitDetails("24-65-9", true);
    }

    //@Test
    public void shouldUpdateVisit_success(){

    }

    //@Test
    public void shouldEndVisit(){

    }
}