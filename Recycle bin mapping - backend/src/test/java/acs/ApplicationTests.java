package acs;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.PostConstruct;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.HashMap;

import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import acs.boundaries.ActionBoundary;
import acs.boundaries.ElementBoundary;
import acs.boundaries.ElementIdBoundary;
import acs.boundaries.UserBoundary;
import acs.boundaries.UserTypeEnum;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ApplicationTests {
	private int port;
	private String url;
	private RestTemplate restTemplate;
	
	//CONST VARS FOR TESTING
	private final String TEST_ADMIN_EMAIL = "testsAdmin@email.com";
	private final String TEST_ADMIN_NAME = "test admin";
	private final String TEST_MANAGER_EMAIL = "testsManager@email.com";
	private final String TEST_MANAGER_NAME = "test manager";
	private final String TEST_PLAYER_EMAIL = "testPlayer@email.com";
	private final String TEST_PLAYER_NAME = "test player";
	private final String TEST_AVATAR = ":)";
	private final UserInfo TEST_USER_INFO = new UserInfo(this.TEST_PLAYER_EMAIL);
	
	//PATH CONSTS
	//TODO create constants of the whole paths would be better
	private final String ACS_PATH = "/acs";
	private final String USERS_PATH = "/users";
	private final String LOGIN_PATH = "/login/{userEmail}";
	private final String ELEMENTS_PATH = "/elements";
	private final String ELEMENT_CHILDREN_PATH = "/{parentElementId}/children";
	private final String ELEMENT_PARENTS_PATH = "/{childElementId}/parents";
	private final String SEARCH_PATH = "/search";
	private final String BY_NAME_PATH = "/byName/{elementName}";
	private final String BY_TYPE_PATH = "/byType/{elementType}";
	private final String PAGE_SIZE = "size={size}";
	private final String PAGE_NUM = "page={page}";
	private final String ACTIONS_PATH = "/actions";
	private final String ADMIN_PATH = "/admin";
	
	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
	}

	@PostConstruct
	public void init() {
		restTemplate = new RestTemplate();
		url = "http://localhost:" + port + ACS_PATH;
	}

	@BeforeEach
	public void setup() {
		registerTestAdminUser();
		registerTestManagerUser();
	}
	
	@AfterEach
	public void teardown() {
		deleteAllActions();
		deleteAllElements();
		deleteAllUsers();
	}
	
	//----------------------------------------------Test helpers----------------------------------------------------------------
	
	private UserBoundary registerTestAdminUser() {
		UserBoundary userToPost = new UserBoundary(TEST_ADMIN_EMAIL, UserTypeEnum.ADMIN, TEST_ADMIN_NAME,
				TEST_AVATAR);

		return restTemplate.postForObject(url + USERS_PATH, userToPost, UserBoundary.class);
	}
	
	private UserBoundary registerTestManagerUser() {
		UserBoundary userToPost = new UserBoundary(TEST_MANAGER_EMAIL, UserTypeEnum.MANAGER, TEST_MANAGER_NAME,
				TEST_AVATAR);

		return restTemplate.postForObject(url + USERS_PATH, userToPost, UserBoundary.class);
	}
	
	private UserBoundary registerTestPlayerUser() {
		UserBoundary userToPost = new UserBoundary(TEST_PLAYER_EMAIL, UserTypeEnum.PLAYER, TEST_PLAYER_NAME, TEST_AVATAR);
		return restTemplate.postForObject(url + USERS_PATH, userToPost, UserBoundary.class);
	}
	
	private void deleteAllActions() {
		restTemplate.delete(url + ADMIN_PATH + ACTIONS_PATH + "/" + TEST_ADMIN_EMAIL);
	}
	
	private void deleteAllUsers() {
		restTemplate.delete(url + ADMIN_PATH + USERS_PATH + "/" + TEST_ADMIN_EMAIL);
	}
	
	private void deleteAllElements() {
		restTemplate.delete(url + ADMIN_PATH + ELEMENTS_PATH + "/" + TEST_ADMIN_EMAIL);
	}
	
	//----------------------------------------------Make sure tests works----------------------------------------------------------------
	@Test
	public void testDummy() {

	}
	
	//----------------------------------------------Element tests----------------------------------------------------------------
	@Test
	public void testGetAllElementsOnServerInitReturnsEmptyArray() throws Exception {
		ElementBoundary[] allElements = restTemplate.getForObject(url + ELEMENTS_PATH + "/" + TEST_MANAGER_EMAIL,
				ElementBoundary[].class);

		assertThat(allElements).isEmpty();
	}
	
	@Test
	public void testGetAllElemnentsOnServer() throws Exception{
		ElementBoundary firstElement = new ElementBoundary("1",
				"TEST",
				"elementTest",
				true,
				new Date(),
				TEST_USER_INFO,
				new Location(2892.3,837387.2),
				new HashMap<>());
		ElementBoundary secondElement = new ElementBoundary("2",
				"TEST",
				"elementTest",
				true,
				new Date(),
				TEST_USER_INFO,
				new Location(2892.3,837387.2),
				new HashMap<>());
		
		ElementBoundary firstElementToServer = restTemplate.postForObject(url + ELEMENTS_PATH + "/" + TEST_MANAGER_EMAIL,
				firstElement, ElementBoundary.class);

		ElementBoundary secondElementToServer = restTemplate.postForObject(url + ELEMENTS_PATH + "/" + TEST_MANAGER_EMAIL,
				secondElement, ElementBoundary.class);
		
		ElementBoundary[] elementsArr= {firstElementToServer,secondElementToServer};
	
		ElementBoundary[] allElements = 
		  this.restTemplate
			.getForObject(
					url + ELEMENTS_PATH +"/" + TEST_MANAGER_EMAIL + "?" + PAGE_SIZE + "&" + PAGE_NUM, 
					ElementBoundary[].class, 2, 0);
		
		assertEquals(elementsArr.length, allElements.length);
		
		
		//not sure the server will return the elements in the same order
		for(int i=0 ; i < elementsArr.length; i++) {
			assertThat(allElements[i].getElementId()).isEqualTo(elementsArr[i].getElementId());
		}
	}
	
	
	@Test
	public void testGetSpecificElementById() throws Exception{	
		ElementBoundary element = new ElementBoundary("1",
				"TEST",
				"elementTest",
				true,
				new Date(),
				TEST_USER_INFO,
				new Location(2892.3,837387.2),
				new HashMap<>());
		
		ElementBoundary elementToServer = restTemplate.postForObject(url + ELEMENTS_PATH + "/" + TEST_MANAGER_EMAIL,
				element, ElementBoundary.class);	

		ElementBoundary elementFromdb = 
		  restTemplate
			.getForObject(
					url+ ELEMENTS_PATH + "/" + TEST_MANAGER_EMAIL+"/{elementId}", 
					ElementBoundary.class,elementToServer.getElementId());
		
		assertThat(elementToServer)
		.isEqualToComparingOnlyGivenFields(elementFromdb, 
				"elementId","type", "name", "active", "elementAttributes");

	
	assertEquals(elementToServer.getLocation().getLat(), elementFromdb.getLocation().getLat());
	assertEquals(elementToServer.getLocation().getLng(), elementFromdb.getLocation().getLng());

	}
	
	@Test
	public void testGetSpecificElementByIdThatDidntExist() throws Exception{
		HttpStatus res = null;
		try {
			restTemplate.getForEntity(url + ELEMENTS_PATH + "/" + TEST_MANAGER_EMAIL + "/{x}",
					String.class, "1");
		} catch (HttpStatusCodeException ex) {
			res = ex.getStatusCode();
		}
		assertEquals(res, HttpStatus.NOT_FOUND);

	}
	
	@Test
	public void testGetAllElementsWithWrongUrl() throws Exception{
		HttpStatus res = null;
		try {
			restTemplate.getForEntity(url + ELEMENTS_PATH + "/",
					String.class);
		} catch (HttpStatusCodeException ex) {
			res = ex.getStatusCode();
		}
		assertEquals(res, HttpStatus.NOT_FOUND);
	}
	
	@Test
	public void testGetElementsSearchByName() throws Exception {
		String nameToSearch = "testName";
		String anotherName = "anotherName";
		ElementBoundary element = new ElementBoundary("1",
				"TEST",
				nameToSearch,
				true,
				new Date(),
				TEST_USER_INFO,
				new Location(2892.3,837387.2),
				new HashMap<>());
		
		ElementBoundary elements[] = new ElementBoundary[3];
		elements[0] = restTemplate.postForObject(url + ELEMENTS_PATH + "/" + TEST_MANAGER_EMAIL,
				element, ElementBoundary.class);
		
		elements[1] = restTemplate.postForObject(url + ELEMENTS_PATH + "/" + TEST_MANAGER_EMAIL,
				element, ElementBoundary.class);
		
		element.setName(anotherName);
		
		elements[2] = restTemplate.postForObject(url + ELEMENTS_PATH + "/" + TEST_MANAGER_EMAIL,
				element, ElementBoundary.class);
		
		ElementBoundary[] searchResult = restTemplate.getForObject(url + ELEMENTS_PATH + "/" + 
				TEST_MANAGER_EMAIL + SEARCH_PATH + BY_NAME_PATH + "?" + PAGE_SIZE + "&" + PAGE_NUM,
				ElementBoundary[].class,nameToSearch, "3", "0");
		
		assertEquals(2, searchResult.length);
		
		for(int i=0 ; i < searchResult.length; i++) {
			assertEquals(nameToSearch, searchResult[i].getName());
		}	
	}
	
	@Test
	public void testGetElementsSearchByType() throws Exception {
		String typeToSearch = "type";
		String anotherType = "anotherType";
		ElementBoundary element = new ElementBoundary("1",
				typeToSearch,
				"testName",
				true,
				new Date(),
				TEST_USER_INFO,
				new Location(2892.3,837387.2),
				new HashMap<>());
		
		ElementBoundary elements[] = new ElementBoundary[3];
		elements[0] = restTemplate.postForObject(url + ELEMENTS_PATH + "/" + TEST_MANAGER_EMAIL,
				element, ElementBoundary.class);
		
		elements[1] = restTemplate.postForObject(url + ELEMENTS_PATH + "/" + TEST_MANAGER_EMAIL,
				element, ElementBoundary.class);
		
		element.setType(anotherType);
		
		elements[2] = restTemplate.postForObject(url + ELEMENTS_PATH + "/" + TEST_MANAGER_EMAIL,
				element, ElementBoundary.class);
		
		ElementBoundary[] searchResult = restTemplate.getForObject(url + ELEMENTS_PATH + "/" + 
				this.TEST_MANAGER_EMAIL + SEARCH_PATH + BY_TYPE_PATH + "?" + PAGE_SIZE + "&" + PAGE_NUM,
				ElementBoundary[].class,typeToSearch, "3", "0");
		
		assertEquals(2, searchResult.length);
		
		for(int i=0 ; i < searchResult.length; i++) {
			assertEquals(typeToSearch, searchResult[i].getType());
		}	
	}
	
	@Test
	public void testPostElement() throws Exception{
		ElementBoundary element = new ElementBoundary("1",
		"TEST",
		"elementTest",
		true,
		new Date(),
		TEST_USER_INFO,
		new Location(2892.3,837387.2),
		new HashMap<>());
		
		ElementBoundary elementFromServer = 
		  this.restTemplate
			.postForObject(
					this.url+ ELEMENTS_PATH + "/" + TEST_MANAGER_EMAIL, 
					element, 
					ElementBoundary.class);
		
		assertThat(elementFromServer)
			.isEqualToComparingOnlyGivenFields(element, 
					"type", "name", "active", "elementAttributes");

		
		assertEquals(element.getLocation().getLat(), elementFromServer.getLocation().getLat());
		assertEquals(element.getLocation().getLng(), elementFromServer.getLocation().getLng());
	}
	
	@Test
	public void testPostElementGeneratesId() throws Exception{
		ElementBoundary element = new ElementBoundary(null,
		"TEST",
		"elementTest",
		true,
		new Date(),
		TEST_USER_INFO,
		new Location(2892.3,837387.2),
		new HashMap<>());
		
		ElementBoundary elementFromServer = 
		  this.restTemplate
			.postForObject(
					url+ ELEMENTS_PATH + "/" + TEST_MANAGER_EMAIL, 
					element, 
					ElementBoundary.class);
		
		assertNotNull(elementFromServer.getElementId());
		
		assertThat(elementFromServer)
			.isEqualToComparingOnlyGivenFields(element, 
					"type", "name", "active", "elementAttributes");
		
		assertEquals(element.getLocation().getLat(), elementFromServer.getLocation().getLat());
		assertEquals(element.getLocation().getLng(), elementFromServer.getLocation().getLng());
	}
	
	@Test
	public void testPostElementByEmailAndIncorrectUrl() throws Exception{
		ElementBoundary element = new ElementBoundary(null,
				"TEST",
				"elementTest",
				true,
				new Date(),
				TEST_USER_INFO,
				new Location(2892.3,837387.2),
				new HashMap<>());
		
		HttpStatus res = null;
		try {
			restTemplate.postForEntity(url + ELEMENTS_PATH + "/", element,
				String.class);	
		} catch (HttpStatusCodeException ex) {
			res = ex.getStatusCode();
		}
		
		assertEquals(HttpStatus.NOT_FOUND, res);
	}
	
	@Test
	public void testPostElementByEmailWithoutName() throws Exception {
		ElementBoundary element = new ElementBoundary(null,
				"TEST",
				null,
				true,
				new Date(),
				TEST_USER_INFO,
				new Location(2892.3,837387.2),
				new HashMap<>());
		
		HttpStatus res = null;
		try {
		restTemplate.postForEntity(url + ELEMENTS_PATH + "/" + TEST_MANAGER_EMAIL, element,
				String.class);	
		} catch (HttpStatusCodeException ex) {
			res = ex.getStatusCode();	
		}
		assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, res);
	}
	
	@Test
	public void testPostElementByEmailWithoutType() throws Exception{
		ElementBoundary element = new ElementBoundary(null,
				null,
				"Liz",
				true,
				new Date(),
				TEST_USER_INFO,
				new Location(2892.3,837387.2),
				new HashMap<>());
		
		HttpStatus res = null;
		try {
		restTemplate.postForEntity(url + ELEMENTS_PATH + "/" + TEST_MANAGER_EMAIL, element,
				String.class);	
		} catch (HttpStatusCodeException ex) {
			res = ex.getStatusCode();	
		}
		assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, res);
	}
	
	@Test
	public void testUpdateElementNameAndValidateDatabaseIsUpdated() throws Exception {
		ElementBoundary element = new ElementBoundary("1",
				"TEST",
				"elementTest",
				true,
				new Date(),
				this.TEST_USER_INFO,
				new Location(2892.3,837387.2),
				new HashMap<>());
				
		ElementBoundary newElement = restTemplate
				.postForObject(
						url + ELEMENTS_PATH + "/" +this.TEST_MANAGER_EMAIL, 
						element, 
						ElementBoundary.class);
				
		newElement.setName("updateElementTest");
		this.restTemplate
			.put(url + ELEMENTS_PATH + "/" + TEST_MANAGER_EMAIL + "/{elementId}", newElement, newElement.getElementId());
		
		assertThat(this.restTemplate
				.getForObject(
						url + ELEMENTS_PATH + "/" + TEST_MANAGER_EMAIL + "/{elementId}", ElementBoundary.class, newElement.getElementId()))
			.extracting("elementId",
					"name")
			.containsExactly(newElement.getElementId(),
					"updateElementTest");
	}
	
	@Test
	public void testUpdateElementIdAndValidateDatabaseIsNotUpdated() throws Exception {
		ElementBoundary element = new ElementBoundary("1",
				"TEST",
				"elementTest",
				true,
				new Date(),
				TEST_USER_INFO,
				new Location(2892.3,837387.2),
				new HashMap<>());
				
		ElementBoundary newElement =  restTemplate
				.postForObject(
						url+"/elements/"+TEST_MANAGER_EMAIL, 
						element, 
						ElementBoundary.class);
				
		element.setElementId("222");

		restTemplate.put(url + ELEMENTS_PATH + "/" + TEST_MANAGER_EMAIL + "/{elementId}", element, newElement.getElementId());
		
		HttpStatus res = null;
		try {
			restTemplate.getForEntity(url + ELEMENTS_PATH + "/" + TEST_MANAGER_EMAIL + "/{elementId}",
					ElementBoundary.class, element.getElementId());
		} catch (HttpStatusCodeException ex) {
			res = ex.getStatusCode();
		}
		assertEquals(HttpStatus.NOT_FOUND, res);
	}
	
	@Test
	public void testUpdateElementDateAndValidateDatabaseIsNotUpdated() throws Exception {
		ElementBoundary element = new ElementBoundary("1",
				"TEST",
				"elementTest",
				true,
				new Date(),
				TEST_USER_INFO,
				new Location(2892.3,837387.2),
				new HashMap<>());
				
		ElementBoundary newElement = restTemplate
				.postForObject(
						url+ELEMENTS_PATH + "/" + TEST_MANAGER_EMAIL, 
						element, 
						ElementBoundary.class);
				
		element.setCreatedTimestamp(new Date(newElement.getCreatedTimestamp().getTime() - 10));
		
		restTemplate.put(url + ELEMENTS_PATH + "/" + TEST_MANAGER_EMAIL + "/{elementId}", element, newElement.getElementId());
		
		ElementBoundary updatedElement = restTemplate.getForObject(url + ELEMENTS_PATH + "/" + TEST_MANAGER_EMAIL + "/{elementId}",
				ElementBoundary.class, newElement.getElementId());

		assertNotEquals(element.getCreatedTimestamp().getTime(), updatedElement.getCreatedTimestamp().getTime());
	}	

	@Test
	public void testBindElementChildWithParantIdAndGetChildren() throws Exception{
		ElementBoundary parantElement = new ElementBoundary("1",
				"TEST",
				"elementTest",
				true,
				new Date(),
				TEST_USER_INFO,
				new Location(2892.3,837387.2),
				new HashMap<>());
		ElementBoundary childElement = new ElementBoundary("2",
				"TEST",
				"elementTest",
				true,
				new Date(),
				TEST_USER_INFO,
				new Location(2892.3,837387.2),
				new HashMap<>());
		
		ElementBoundary parantElementToServer = restTemplate.postForObject(url+ ELEMENTS_PATH + "/" + TEST_MANAGER_EMAIL,
				parantElement, ElementBoundary.class);

		ElementBoundary childElementToServer = restTemplate.postForObject(url+ ELEMENTS_PATH + "/" + TEST_MANAGER_EMAIL,
				childElement, ElementBoundary.class);
		
		ElementIdBoundary childIdBoundary = new ElementIdBoundary();
		childIdBoundary.setId(childElementToServer.getElementId());

		restTemplate.put(url + ELEMENTS_PATH + "/" + TEST_MANAGER_EMAIL + ELEMENT_CHILDREN_PATH, childIdBoundary, parantElementToServer.getElementId());

		ElementBoundary[] childrenFromServer = 
		  restTemplate.getForObject(url+ ELEMENTS_PATH +"/" + TEST_MANAGER_EMAIL + ELEMENT_CHILDREN_PATH,ElementBoundary[].class,parantElementToServer.getElementId());
		
		assertEquals(1, childrenFromServer.length);
		assertEquals(childElementToServer.getElementId(), childrenFromServer[0].getElementId());
	}

	@Test
	public void testBindElementToItself() throws Exception{
		ElementBoundary parentElement = new ElementBoundary("1",
				"TEST",
				"elementTest",
				true,
				new Date(),
				TEST_USER_INFO,
				new Location(2892.3,837387.2),
				new HashMap<>());
	
		
		ElementBoundary parentElementToServer = restTemplate.postForObject(url + ELEMENTS_PATH + "/" + TEST_MANAGER_EMAIL,
				parentElement, ElementBoundary.class);

		
		ElementIdBoundary parentIdBoundary = new ElementIdBoundary();
		parentIdBoundary.setId(parentElementToServer.getElementId());
		
		HttpStatus res = null;
		try{
			restTemplate
				.put(url + ELEMENTS_PATH + "/" + TEST_ADMIN_EMAIL+"/{parentElementId}/children",
					parentIdBoundary, parentElementToServer.getElementId());
		
		} catch (HttpStatusCodeException ex) {
			res = ex.getStatusCode();	
		}
		assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, res);
	}
	
	@Test
	public void testGetElementChildrenWithParantIdThatNotExist() throws Exception{
		ElementBoundary parantElement = new ElementBoundary("1",
				"TEST",
				"elementTest",
				true,
				new Date(),
				TEST_USER_INFO,
				new Location(2892.3,837387.2),
				new HashMap<>());
		ElementBoundary childElement = new ElementBoundary("2",
				"TEST",
				"elementTest",
				true,
				new Date(),
				TEST_USER_INFO,
				new Location(2892.3,837387.2),
				new HashMap<>());
		
		ElementBoundary parantElementToServer = restTemplate.postForObject(url+ ELEMENTS_PATH + "/" + TEST_MANAGER_EMAIL,
				parantElement, ElementBoundary.class);

		ElementBoundary childElementToServer = restTemplate.postForObject(url+ ELEMENTS_PATH + "/" + TEST_MANAGER_EMAIL,
				childElement, ElementBoundary.class);
		
		ElementIdBoundary childIdBoundary = new ElementIdBoundary();
		childIdBoundary.setId(childElementToServer.getElementId());

		restTemplate.put(url + ELEMENTS_PATH + "/" +  TEST_MANAGER_EMAIL + ELEMENT_CHILDREN_PATH , childIdBoundary, parantElementToServer.getElementId());
		
		HttpStatus res = null;
		try {
			restTemplate.getForObject(url + ELEMENTS_PATH + "/" + TEST_MANAGER_EMAIL + ELEMENT_CHILDREN_PATH + "?" + PAGE_NUM + "&" + PAGE_SIZE, ElementBoundary[].class,"6666", 0 , 2);	
		} catch (HttpStatusCodeException ex) {
			res = ex.getStatusCode();	
		}
		assertEquals(HttpStatus.NOT_FOUND, res);
		
	}
	
	@Test
	public void testGetElementParentWithChildIdThatNotExist() throws Exception{
		ElementBoundary parantElement = new ElementBoundary("1",
				"TEST",
				"elementTest",
				true,
				new Date(),
				TEST_USER_INFO,
				new Location(2892.3,837387.2),
				new HashMap<>());
		ElementBoundary childElement = new ElementBoundary("2",
				"TEST",
				"elementTest",
				true,
				new Date(),
				TEST_USER_INFO,
				new Location(2892.3,837387.2),
				new HashMap<>());
		
		ElementBoundary parantElementToServer = restTemplate.postForObject(url + ELEMENTS_PATH + "/" + TEST_MANAGER_EMAIL,
				parantElement, ElementBoundary.class);

		ElementBoundary childElementToServer = restTemplate.postForObject(url + ELEMENTS_PATH + "/" + TEST_MANAGER_EMAIL,
				childElement, ElementBoundary.class);
		
		ElementIdBoundary childIdBoundary = new ElementIdBoundary();
		childIdBoundary.setId(childElementToServer.getElementId());

		restTemplate
			.put(url + ELEMENTS_PATH + "/" + TEST_ADMIN_EMAIL + ELEMENT_CHILDREN_PATH,
					childIdBoundary, parantElementToServer.getElementId());
		
		HttpStatus res = null;
		try {
			 restTemplate.getForObject(url + ELEMENTS_PATH + "/" + TEST_MANAGER_EMAIL + ELEMENT_PARENTS_PATH + "?" + PAGE_NUM + "&" + PAGE_SIZE,
					 ElementBoundary[].class,"6666", 0 , 2);	
		} catch (HttpStatusCodeException ex) {
			res = ex.getStatusCode();	
		}
		assertEquals(HttpStatus.NOT_FOUND, res);
	}
	
	@Test
	public void testBindElementChildWithParantIdAndGetParent() throws Exception{
		ElementBoundary parantElement = new ElementBoundary("1",
				"TEST",
				"elementTest",
				true,
				new Date(),
				TEST_USER_INFO,
				new Location(2892.3,837387.2),
				new HashMap<>());
		ElementBoundary childElement = new ElementBoundary("2",
				"TEST",
				"elementTest",
				true,
				new Date(),
				TEST_USER_INFO,
				new Location(2892.3,837387.2),
				new HashMap<>());
		
		ElementBoundary parantElementToServer = restTemplate.postForObject(url + ELEMENTS_PATH +"/" + TEST_MANAGER_EMAIL,
				parantElement, ElementBoundary.class);

		ElementBoundary childElementToServer = restTemplate.postForObject(url + ELEMENTS_PATH + "/" + TEST_MANAGER_EMAIL,
				childElement, ElementBoundary.class);


		ElementIdBoundary childIdBoundary = new ElementIdBoundary();
		childIdBoundary.setId(childElementToServer.getElementId());

		restTemplate.put(url + ELEMENTS_PATH + "/" + TEST_ADMIN_EMAIL + ELEMENT_CHILDREN_PATH,
					childIdBoundary, parantElementToServer.getElementId());
		
		ElementBoundary[] elementsFromServer = 
		  this.restTemplate
			.getForObject(
					this.url+"/elements/" + this.TEST_MANAGER_EMAIL + ELEMENT_PARENTS_PATH + "?" + PAGE_NUM + "&" + PAGE_SIZE, 
					ElementBoundary[].class,childElementToServer.getElementId(), 2, 0);
		
		assertEquals(1, elementsFromServer.length);
		assertEquals(parantElementToServer.getElementId(), elementsFromServer[0].getElementId());
		
	}
	
	
	//----------------------------------------------Actions tests------------------------------------------------------------------
	@Test
	public void testPostAction() throws Exception {
		ActionBoundary action = prepareActionTestAndReturnAction();
		
		ActionBoundary actionFromServer = 
		  restTemplate
			.postForObject(
					url + ACTIONS_PATH, 
					action, 
					ActionBoundary.class);
		
		assertThat(actionFromServer)
			.isEqualToComparingOnlyGivenFields(action, 
					"type", "actionAttributes");
	
	}
	
	@Test
	public void testPostActionNull() throws Exception{
		HttpStatus res = null;
		try {
			restTemplate.postForObject(
							url + ACTIONS_PATH, 
							null, 
							ActionBoundary.class);	
		} catch (HttpStatusCodeException ex) {
			res = ex.getStatusCode();	
		}
		assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, res);
	}
	
	@Test
	public void testPostInvokeActionByEmailAndIdCreateAutomaticaly() throws Exception {
		ActionBoundary action = prepareActionTestAndReturnAction();
		ActionBoundary actionFromServer = 
		  restTemplate.postForObject(url + ACTIONS_PATH, action, ActionBoundary.class);
		
		assertNotNull(actionFromServer.getActionId());
	}
	
	public ActionBoundary prepareActionTestAndReturnAction() {
		registerTestPlayerUser();
		UserInfo player = TEST_USER_INFO;
		ElementBoundary element = new ElementBoundary("2",
				"TEST",
				"elementTest",
				true,
				new Date(),
				TEST_USER_INFO,
				new Location(2892.3,837387.2),
				new HashMap<>());
		
		ElementBoundary elementToServer = restTemplate.postForObject(url + ELEMENTS_PATH + "/" + TEST_MANAGER_EMAIL,
				element, ElementBoundary.class);
		
		ActionBoundary action = new ActionBoundary(null,
				"TEST",
				new ElementInfo(elementToServer.getElementId()),
				new Date(),
				player,
				new HashMap<>());
		
		return action;
	}
	
	
//---------------------------------------------- Admin tests ---------------------------------------------------------------------
	@Test
	public void testGetListOfUsers() throws Exception{
		testPostUser();
		UserBoundary[] usersFromServer = restTemplate
				.getForObject(
						url + ADMIN_PATH + USERS_PATH + "/" + TEST_ADMIN_EMAIL + "?" +PAGE_SIZE + "&" + PAGE_NUM, UserBoundary[].class, 3, 0);
		
		assertEquals(3, usersFromServer.length);
	}
	
	@Test
	public void testGetListOfUsersWithManagerEmailForbidden() throws Exception{
		
		HttpStatus res = null;
		try {
			restTemplate
			.getForObject(
					url + ADMIN_PATH + USERS_PATH + "/" + TEST_MANAGER_EMAIL + "?" +PAGE_SIZE + "&" + PAGE_NUM, UserBoundary[].class, 3, 0);
		} catch (HttpStatusCodeException ex) {
			res = ex.getStatusCode();	
		}
		assertEquals(HttpStatus.FORBIDDEN, res);
	}
	
	@Test
	public void testGetListOfUsersWithPlayerEmailForbidden() throws Exception{
		
		HttpStatus res = null;
		registerTestPlayerUser();
		res = null;
		try {
			restTemplate
			.getForObject(
					url + ADMIN_PATH + USERS_PATH + "/" + TEST_PLAYER_EMAIL + "?" +PAGE_SIZE + "&" + PAGE_NUM, UserBoundary[].class, 3, 0);
		} catch (HttpStatusCodeException ex) {
			res = ex.getStatusCode();	
		}
		assertEquals(HttpStatus.FORBIDDEN, res);
		
	}
	
	@Test
	public void testGetListOfActions() throws Exception{
		testPostAction();
		
		assertThat(restTemplate
				.getForObject(
						url + ADMIN_PATH + ACTIONS_PATH + "/" + TEST_ADMIN_EMAIL + "?" +PAGE_SIZE + "&" + PAGE_NUM, ActionBoundary[].class, 3, 0)).isNotEmpty();
		}	
	
	@Test
	public void testGetListOfActionsIsEmpty() throws Exception{
		assertThat(restTemplate
				.getForObject(
						url + ADMIN_PATH + ACTIONS_PATH + "/" + TEST_ADMIN_EMAIL + "?" +PAGE_SIZE + "&" + PAGE_NUM, ActionBoundary[].class, 3, 0)).isEmpty();
		}
	
	@Test
	public void testDeleteListOfActions() throws Exception{
		deleteAllActions();
		testGetListOfActionsIsEmpty();
	}
	
	@Test
	public void testDeleteListOfElements() throws Exception{
		deleteAllElements();
		testGetAllElementsOnServerInitReturnsEmptyArray();
	}	
	
	@Test
	public void testDeleteListOfUsers() throws Exception{
		deleteAllUsers();
		
		registerTestAdminUser();
		

		UserBoundary[] onlyNewAdmin = restTemplate
				.getForObject(url + ADMIN_PATH + USERS_PATH + "/" + TEST_ADMIN_EMAIL,UserBoundary[].class);
		
		assertEquals(1, onlyNewAdmin.length);
		assertEquals(TEST_ADMIN_EMAIL, onlyNewAdmin[0].getEmail());
		
	}	
	
	
//---------------------------------------------User tests---------------------------------------------------------------------
	@Test
	public void testPostUser() throws Exception{
		UserBoundary user = new UserBoundary(TEST_PLAYER_EMAIL, UserTypeEnum.PLAYER, TEST_PLAYER_NAME, TEST_AVATAR);
		UserBoundary userFromServer = 
		  this.restTemplate
			.postForObject(
					url + USERS_PATH, 
					user, 
					UserBoundary.class);
		
		assertThat(userFromServer)
			.isEqualToComparingOnlyGivenFields(user, 
					"email","role","username", "avatar");
	}
	
	@Test
	public void testUpdateValueAttributeOfANewlyCreatedUserAndValidateDatabaseIsUpdated() throws Exception {
		UserBoundary newUser = registerTestPlayerUser();
		newUser.setUsername("otherName");
		restTemplate
			.put(url + USERS_PATH + "/{userEmail}", newUser, newUser.getEmail());

		assertThat(restTemplate
				.getForObject(
						url + USERS_PATH + LOGIN_PATH, UserBoundary.class, newUser.getEmail()))
			.extracting(
					"email", 
					"role", 
					"username", 
					"avatar")
			.containsExactly(
					newUser.getEmail(),
					newUser.getRole(),
					"otherName",
					newUser.getAvatar());
	}
	
	@Test
	public void testGetValidUserAndRetrieveUserDetails() throws Exception {
		UserBoundary userFromPost = registerTestPlayerUser();
		
		UserBoundary userFromGet = 
				  restTemplate
					.getForObject(
							url + USERS_PATH + LOGIN_PATH, UserBoundary.class, userFromPost.getEmail());
				
		assertThat(userFromGet)
		.extracting(
				"email", 
				"role", 
				"username", 
				"avatar")
		.containsExactly(
				userFromPost.getEmail(),
				userFromPost.getRole(),
				userFromPost.getUsername(),
				userFromPost.getAvatar());
	}
	
	@Test
	public void testGetUserWithWrongEmail() throws Exception {
		registerTestPlayerUser();
		HttpStatus res = null;
		try {
		String  notExistingEmail = "notExist@email.com";
		restTemplate.getForObject(url + USERS_PATH + LOGIN_PATH, UserBoundary.class, notExistingEmail);
		}catch (HttpStatusCodeException ex) {
			res = ex.getStatusCode();	
		}
		assertEquals(HttpStatus.NOT_FOUND, res);
	}
	
	@Test
	public void testPostUserWithInvalidEmail() throws Exception {
		UserBoundary user = new UserBoundary("testEmailgmail.com",UserTypeEnum.PLAYER, TEST_PLAYER_NAME, TEST_AVATAR);
		
		HttpStatus res = null;
		try {
			restTemplate
			.postForObject(
					url + USERS_PATH, user, UserBoundary.class);
		
		}catch (HttpStatusCodeException ex) {
			res = ex.getStatusCode();	
		}
		assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, res);
	}
	
	@Test
	public void testPostUserWithNullType() throws Exception {

		UserBoundary user = new UserBoundary(TEST_PLAYER_EMAIL, null, TEST_PLAYER_NAME, TEST_AVATAR);
		HttpStatus res = null;
		try {
			restTemplate
			.postForObject(
					url + USERS_PATH, user, UserBoundary.class);
		
		}catch (HttpStatusCodeException ex) {
			res = ex.getStatusCode();	
		}
		assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, res);
	}
	
	@Test
	public void testPostValidUserWithNullAvatar() throws Exception {
		UserBoundary user = new UserBoundary(TEST_PLAYER_EMAIL, UserTypeEnum.PLAYER, TEST_PLAYER_NAME, null);
		HttpStatus res = null;
		try {
			restTemplate
			.postForObject(
					url + USERS_PATH, user, UserBoundary.class);
		} catch (HttpStatusCodeException ex) {
			res = ex.getStatusCode();	
		}
		assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, res);
	}
	
	@Test
	public void testPostUserThatAlreadyExist() throws Exception {

		UserBoundary user = registerTestPlayerUser();
		user.setUsername("another user name");
		user.setRole(UserTypeEnum.MANAGER);
			
		HttpStatus res = null;
		try {
			restTemplate
			.postForObject(
					url + USERS_PATH, user, UserBoundary.class);
		} catch (HttpStatusCodeException ex) {
			res = ex.getStatusCode();	
		}
		assertEquals(HttpStatus.FORBIDDEN, res);
	}
	
	@Test
	public void testUpdateUserAndTryToChangeTheEmail() throws Exception {
		UserBoundary newUser = registerTestPlayerUser();
		newUser.setEmail("testEmailChange@gmail.com");
		newUser.setUsername("newUserName");
		HttpStatus res = null;
		
		try {
		restTemplate
		.put(url + USERS_PATH + "/{userEmail}", newUser, TEST_PLAYER_EMAIL);
		} catch (HttpStatusCodeException ex) {
			res = ex.getStatusCode();
		}
		
		assertThat(restTemplate
				.getForObject(url + USERS_PATH + LOGIN_PATH, UserBoundary.class, TEST_PLAYER_EMAIL)
				.getEmail())
				.isEqualTo(TEST_PLAYER_EMAIL);
		
		try {
			restTemplate
			.getForObject(
					url + USERS_PATH + LOGIN_PATH, UserBoundary.class, newUser.getEmail());
		} catch (HttpStatusCodeException ex) {
			res = ex.getStatusCode();
		}
		assertEquals(HttpStatus.NOT_FOUND, res);	
	}
	
	@Test
	public void testUpdateUserNameForUserNotExist() throws Exception {
		UserBoundary newUser = registerTestPlayerUser();

		newUser.setUsername("test");
		HttpStatus res = null;
		try {
			this.restTemplate
			.put(this.url + USERS_PATH +"/{userEmail}", newUser, "noExist@gmail.com");
		} catch (HttpStatusCodeException ex) {
			res = ex.getStatusCode();
		}
		assertEquals(HttpStatus.NOT_FOUND, res);
	}
}
