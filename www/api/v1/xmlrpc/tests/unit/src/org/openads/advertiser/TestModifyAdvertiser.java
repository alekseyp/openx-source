/*
+---------------------------------------------------------------------------+
| OpenX v${RELEASE_MAJOR_MINOR}                                                                |
| ======${RELEASE_MAJOR_MINOR_DOUBLE_UNDERLINE}                                                                 |
|                                                                           |
| Copyright (c) 2003-2008 OpenX Limited                                     |
| For contact details, see: http://www.openx.org/                         |
|                                                                           |
| This program is free software; you can redistribute it and/or modify      |
| it under the terms of the GNU General Public License as published by      |
| the Free Software Foundation; either version 2 of the License, or         |
| (at your option) any later version.                                       |
|                                                                           |
| This program is distributed in the hope that it will be useful,           |
| but WITHOUT ANY WARRANTY; without even the implied warranty of            |
| MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the             |
| GNU General Public License for more details.                              |
|                                                                           |
| You should have received a copy of the GNU General Public License         |
| along with this program; if not, write to the Free Software               |
| Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA |
+---------------------------------------------------------------------------+
$Id:$
*/

package org.openads.advertiser;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.xmlrpc.XmlRpcException;
import org.openads.config.GlobalSettings;
import org.openads.utils.ErrorMessage;
import org.openads.utils.TextUtils;

/**
 * Verify Modify Advertiser method
 *
 * @author     Andriy Petlyovanyy <apetlyovanyy@lohika.com>
 */
public class TestModifyAdvertiser extends AdvertiserTestCase {
	private Integer advertiserId;

	protected void setUp() throws Exception {
		super.setUp();
		advertiserId = createAdvertiser();
	}

	protected void tearDown() throws Exception {
		deleteAdvertiser(advertiserId);
		super.tearDown();
	}

	/**
	 * Execute test method with error
	 *
	 * @param params -
	 *            parameters for test method
	 * @param errorMsg -
	 *            true error messages
	 * @throws MalformedURLException
	 */
	private void executeModifyAdvertiserWithError(Object[] params,
			String errorMsg) throws MalformedURLException {

		try {
			execute(MODIFY_ADVERTISER_METHOD, params);
			fail(MODIFY_ADVERTISER_METHOD
					+ " executed successfully, but it shouldn't.");
		} catch (XmlRpcException e) {
			assertEquals(ErrorMessage.WRONG_ERROR_MESSAGE, errorMsg, e
					.getMessage());
		}
	}

	/**
	 * Test method with all required fields and some optional.
	 *
	 * @throws XmlRpcException
	 */
	public void testModifyAdvertiserAllReqAndSomeOptionalFields()
			throws XmlRpcException {
		assertNotNull(advertiserId);
		Map<String, Object> struct = new HashMap<String, Object>();
		struct.put(ADVERTISER_ID, advertiserId); // required
		struct.put(ADVERTISER_NAME, "testAdvertiser");
		struct.put(CONTACT_NAME, "advertiserName");
		struct.put(USERNAME, TextUtils.generateUniqueName("advertiserUser"));
		struct.put(PASSWORD, "qwerty");

		Object[] params = new Object[] { sessionId, struct };
		final Boolean result = (Boolean) client.execute(
				MODIFY_ADVERTISER_METHOD, params);
		assertTrue(result);

	}

	/**
	 * Test method without some required fields.
	 *
	 * @throws MalformedURLException
	 */
	public void testModifyAdvertiserWithoutSomeRequiredFields()
			throws MalformedURLException {
		Map<String, Object> struct = new HashMap<String, Object>();
		struct.put(EMAIL_ADDRESS, "test@mail.com");

		Object[] params = new Object[] { sessionId, struct };

		executeModifyAdvertiserWithError(params, ErrorMessage.getMessage(
				ErrorMessage.FIELD_IN_STRUCTURE_DOES_NOT_EXISTS, ADVERTISER_ID));

	}

	/**
	 * Test method with fields that has value greater than max.
	 *
	 * @throws MalformedURLException
	 * @throws XmlRpcException
	 */
	public void testModifyAdvertiserGreaterThanMaxFieldValueError()
			throws MalformedURLException, XmlRpcException {

		final String strGreaterThan255 = TextUtils.getString(256);
		final String strGreaterThan64 = TextUtils.getString(65);

		assertNotNull(advertiserId);

		Map<String, Object> struct = new HashMap<String, Object>();
		Object[] params = new Object[] { sessionId, struct };
		struct.put(ADVERTISER_ID, advertiserId);

		// test advertiserName
		struct.put(ADVERTISER_NAME, strGreaterThan255);
		executeModifyAdvertiserWithError(params, ErrorMessage.getMessage(
				ErrorMessage.EXCEED_MAXIMUM_LENGTH_OF_FIELD, ADVERTISER_NAME));

		// test contactName
		struct.remove(ADVERTISER_NAME);
		struct.put(CONTACT_NAME, strGreaterThan255);
		executeModifyAdvertiserWithError(params, ErrorMessage.getMessage(
				ErrorMessage.EXCEED_MAXIMUM_LENGTH_OF_FIELD, CONTACT_NAME));

		// test username
		struct.remove(CONTACT_NAME);
		struct.put(USERNAME, strGreaterThan64);
		executeModifyAdvertiserWithError(params, ErrorMessage.getMessage(
				ErrorMessage.EXCEED_MAXIMUM_LENGTH_OF_FIELD, USERNAME));

		// test password
		struct.remove(USERNAME);
		struct.put(PASSWORD, strGreaterThan64);
		executeModifyAdvertiserWithError(params, ErrorMessage.getMessage(
				ErrorMessage.EXCEED_MAXIMUM_LENGTH_OF_FIELD, PASSWORD));
	}

	/**
	 * Test method with fields that has value less than min
	 *
	 * @throws MalformedURLException
	 */
	public void testModifyAdvertiserLessThanMinFieldValueError()
			throws MalformedURLException {
		assertNotNull(advertiserId);

		Map<String, Object> struct = new HashMap<String, Object>();
		struct.put(ADVERTISER_ID, advertiserId);

		Object[] params = new Object[] { sessionId, struct };

		struct.put(USERNAME, "");

		executeModifyAdvertiserWithError(params, ErrorMessage.getMessage(
				ErrorMessage.USERNAME_IS_FEWER_THAN, "1"));
	}

	/**
	 * Test method with fields that has min. allowed values.
	 *
	 * @throws XmlRpcException
	 * @throws MalformedURLException
	 */
	public void testModifyAdvertiserMinValues() throws XmlRpcException,
			MalformedURLException {
		Map<String, Object> struct = new HashMap<String, Object>();
		struct.put(ADVERTISER_ID, advertiserId);
		struct.put(ADVERTISER_NAME, "");
		struct.put(CONTACT_NAME, "");
		struct.put(EMAIL_ADDRESS, TextUtils.MIN_ALLOWED_EMAIL);
		struct.put(USERNAME, "s");
		struct.put(PASSWORD, "");
		Object[] params = new Object[] { sessionId, struct };
		final Boolean result = (Boolean) client.execute(
				MODIFY_ADVERTISER_METHOD, params);
		assertTrue(result);
	}

	/**
	 * Test method with fields that has max. allowed values.
	 *
	 * @throws XmlRpcException
	 * @throws MalformedURLException
	 */
	public void testModifyAdvertiserMaxValues() throws XmlRpcException,
			MalformedURLException {
		Map<String, Object> struct = new HashMap<String, Object>();
		struct.put(ADVERTISER_ID, advertiserId);
		struct.put(ADVERTISER_NAME, TextUtils.getString(255));
		struct.put(CONTACT_NAME, TextUtils.getString(255));
		struct.put(EMAIL_ADDRESS, TextUtils.getString(59) + "@a.aa");
		struct.put(USERNAME, System.getProperty("user.name")
				+ TextUtils.getString(64 - System.getProperty("user.name")
						.length()));
		struct.put(PASSWORD, TextUtils.getString(64));
		Object[] params = new Object[] { sessionId, struct };
		final Boolean result = (Boolean) client.execute(
				MODIFY_ADVERTISER_METHOD, params);
		assertTrue(result);
	}

	/**
	 * Try to modify advertiser with unknown id
	 *
	 * @throws XmlRpcException
	 * @throws MalformedURLException
	 */
	public void testModifyAdvertiserUnknownIdError() throws XmlRpcException,
			MalformedURLException {
		final Integer id = createAdvertiser();
		deleteAdvertiser(id);

		Map<String, Object> struct = new HashMap<String, Object>();
		struct.put(ADVERTISER_ID, id);
		Object[] params = new Object[] { sessionId, struct };

		executeModifyAdvertiserWithError(params, ErrorMessage.getMessage(
				ErrorMessage.UNKNOWN_ID_ERROR, ADVERTISER_ID));
	}

	/**
	 * Try to modify advertiser with unknown agency id
	 *
	 * @throws XmlRpcException
	 * @throws MalformedURLException
	 */
	public void testModifyAdvertiserUnknownAgencyIdError()
			throws XmlRpcException, MalformedURLException {
		final Integer id = createAgency();
		deleteAgency(id);

		Map<String, Object> struct = new HashMap<String, Object>();
		struct.put(ADVERTISER_ID, advertiserId);
		struct.put(AGENCY_ID, id);
		Object[] params = new Object[] { sessionId, struct };

		try {
			execute(MODIFY_ADVERTISER_METHOD, params);
			fail(ErrorMessage.METHOD_EXECUTED_SUCCESSFULLY_BUT_SHOULD_NOT_HAVE);
		} catch (XmlRpcException e) {
			assertEquals(ErrorMessage.WRONG_ERROR_MESSAGE, ErrorMessage
					.getMessage(ErrorMessage.UNKNOWN_ID_ERROR, AGENCY_ID), e
					.getMessage());
		}
	}

	/**
	 * Try to modify advertiser with the same username as an existing admin,
	 * agency, advertiser, or publisher username.
	 *
	 * @throws MalformedURLException
	 */
	public void testModifyAdvertiserDuplicateUsernameError()
			throws MalformedURLException {
		Map<String, Object> struct = new HashMap<String, Object>();
		struct.put(ADVERTISER_ID, advertiserId);
		struct.put(USERNAME, GlobalSettings.getUserName());
		Object[] params = new Object[] { sessionId, struct };

		executeModifyAdvertiserWithError(params,
				ErrorMessage.USERNAME_MUST_BE_UNIQUE);
	}

	/**
	 * Try to modify advertiser with username fewer than 1 character.
	 *
	 * @throws MalformedURLException
	 */
	public void testModifyAdvertiserUsernameFormatError1()
			throws MalformedURLException {
		Map<String, Object> struct = new HashMap<String, Object>();
		struct.put(ADVERTISER_ID, advertiserId);
		struct.put(USERNAME, "");
		Object[] params = new Object[] { sessionId, struct };

		executeModifyAdvertiserWithError(params, ErrorMessage.getMessage(
				ErrorMessage.USERNAME_IS_FEWER_THAN, "1"));
	}

	/**
	 * Try to modify advertiser when the username is null and the password is
	 * not.
	 *
	 * @throws MalformedURLException
	 */
	public void testModifyAdvertiserUsernameFormatError2()
			throws MalformedURLException {
		Map<String, Object> struct = new HashMap<String, Object>();
		struct.put(ADVERTISER_ID, advertiserId);
		struct.put(PASSWORD, "password");
		Object[] params = new Object[] { sessionId, struct };

		executeModifyAdvertiserWithError(params,
				ErrorMessage.USERNAME_IS_NULL_AND_THE_PASSWORD_IS_NOT);
	}

	/**
	 * Try to modify advertiser when there is '\' character in the password.
	 *
	 * @throws MalformedURLException
	 */
	public void testModifyAdvertiserPasswordFormatError()
			throws MalformedURLException {
		Map<String, Object> struct = new HashMap<String, Object>();
		struct.put(ADVERTISER_ID, advertiserId);
		struct.put(USERNAME, TextUtils.generateUniqueName("advertiserUser"));
		struct.put(PASSWORD, "password\\");
		Object[] params = new Object[] { sessionId, struct };

		executeModifyAdvertiserWithError(params, ErrorMessage.getMessage(
				ErrorMessage.PASSWORDS_CANNOT_CONTAIN, "\\"));
	}

	/**
	 * Test method with fields that has value of wrong type (error).
	 *
	 * @throws MalformedURLException
	 */
	public void testModifyAdvertiserWrongTypeError()
			throws MalformedURLException {
		Map<String, Object> struct = new HashMap<String, Object>();
		struct.put(ADVERTISER_ID, advertiserId);
		Object[] params = new Object[] { sessionId, struct };

		struct.put(AGENCY_ID, TextUtils.NOT_INTEGER);
		executeModifyAdvertiserWithError(params, ErrorMessage.getMessage(
				ErrorMessage.FIELD_IS_NOT_INTEGER, AGENCY_ID));

		struct.remove(AGENCY_ID);
		struct.put(ADVERTISER_NAME, TextUtils.NOT_STRING);
		executeModifyAdvertiserWithError(params, ErrorMessage.getMessage(
				ErrorMessage.FIELD_IS_NOT_STRING, ADVERTISER_NAME));
	}
}
