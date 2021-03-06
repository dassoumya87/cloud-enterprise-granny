package com.sap.hana.cloud.samples.granny.api.impl;

import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sap.hana.cloud.samples.granny.dao.PhoneNumberValidationDAO;
import com.sap.hana.cloud.samples.granny.dao.PhoneNumberValidationResult;
import com.sap.hana.cloud.samples.granny.model.Address;
import com.sap.hana.cloud.samples.granny.model.Contact;
import com.sap.hana.cloud.samples.granny.srv.ContactService;

/**
 *  Provides the public API for {@link Contact}-related operations and services.
 */
@Service("contactFacade")
@Path("/contacts")
@Produces({ "application/json" })
@com.webcohesion.enunciate.metadata.Facet(value="Addressbook API", documentation="Provides REST endpoints to manage address data.")
@com.webcohesion.enunciate.metadata.rs.ResourceLabel(value="Contact Service")
@com.webcohesion.enunciate.metadata.rs.ServiceContextRoot(value="/api/v1")
public class ContactFacadeImpl extends BaseFacade 
{
	@Autowired
	ContactService contactSrv = null;
	
	@Autowired 
	PhoneNumberValidationDAO phoneNumberValidation = null;
	
	/**
	 * Returns a {@link List} of all {@link Contact} objects.
	 * 
	 * @return {@link Response} representation of the {@link List} of all {@link Contact} objects
	 * 
	 * @name Get All Contacts 
	 */
	@GET
	@com.webcohesion.enunciate.metadata.rs.TypeHint(Contact.class)
	public Response findAll() 
	{
		List<Contact> contacts = contactSrv.getAllContactes();
		return Response.ok(contacts).build();
	}
	
	/**
	 * Creates a new {@link Contact} object.
	 * 
	 * @param contact The {@link Contact} to be created
	 * @return {@link Response} representation of the created {@link Contact}
	 * 
	 * @name Create Contact
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@com.webcohesion.enunciate.metadata.rs.TypeHint(Contact.class)
	public Response create(@Valid Contact contact) 
	{	
		contact = validatePhoneNumbers(contact);
		
		contact = contactSrv.createContact(contact);
		return Response.ok(contact).status(Status.CREATED).build();
	}
	


	/**
	 * Returns the {@link Contact} with the specified ID or a {@link Status.NOT_FOUND} (404) error code if no {@link Contact} object with the specified ID exists.
	 * 
	 * @param id The id of the  {@link Contact} to retrieve
	 * @return {@link Response} representation of the {@link Contact} or a {@link Status.NOT_FOUND} (404) error code
	 * 
	 * @name Get Contact 
	 */
	@GET
	@Path("/{id}")
	@com.webcohesion.enunciate.metadata.rs.TypeHint(Contact.class)
	@com.webcohesion.enunciate.metadata.rs.StatusCodes(value = {@com.webcohesion.enunciate.metadata.rs.ResponseCode(code = 200, condition = "In case of success"),
													   @com.webcohesion.enunciate.metadata.rs.ResponseCode(code = 404, condition = "In case no Contact object with the specified ID exists")})

	public Response findOne(@PathParam("id") String id) 
	{
		Response retVal = null;
		
		Contact contact = contactSrv.getContactById(id);
		
		if (contact == null)
		{
			retVal = Response.status(Status.NOT_FOUND).build();
		}
		else
		{
			retVal = Response.ok(contact).build();
		}
		
		return retVal;
	}
	
	/**
	 * Updates the {@link Contact} object with the specified ID with the properties of the specified {@link Contact}.
	 * 
	 * @param id The ID of the {@link Contact} to return
	 * @param contact The {@link Contact} object with the new property values to be used 
	 * @return {@link Response} representation of the updated {@link Contact}
	 * 
	 * @name Update Contact
	 */
	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@com.webcohesion.enunciate.metadata.rs.TypeHint(Contact.class)
	public Response update(@PathParam("id") String id, @Valid Contact contact) 
	{
		contact = validatePhoneNumbers(contact);
		
		contact = contactSrv.updateContact(contact);
		return Response.ok(contact).build();
	}

	/**
	 * Deletes the {@link Contact} object with the specified ID.
	 * 
	 * @param id The ID of the {@link Contact} to delete
	 * @response {@link Status.OK} (200) status code
	 * 
	 * @name Delete Contact
	 */
	@DELETE
	@Path("/{id}")
	@Consumes({MediaType.WILDCARD})
	@Produces({MediaType.WILDCARD})
	@com.webcohesion.enunciate.metadata.rs.StatusCodes(value = {@com.webcohesion.enunciate.metadata.rs.ResponseCode(code = 200, condition = "In case of success")})
	@com.webcohesion.enunciate.metadata.rs.TypeHint(com.webcohesion.enunciate.metadata.rs.TypeHint.NO_CONTENT.class)
	public Response delete(@PathParam("id") String id) 
	{
		Response retVal = null;
		
		Contact contact = contactSrv.getContactById(id);
		contactSrv.deleteContact(contact);
		
		retVal = Response.status(Status.OK).build();
		
		return retVal;
	}
	
	/**
	 * Returns a {@link List} of all {@link Address} objects for the {@link Contact} with the specified ID.
	 * 
	 * @param id The ID of the {@link Contact}
	 * @return {@link Response} representation of the {@link List} of all {@link Address} objects for the {@link Contact} with the specified ID.
	 * 
	 *  Find Addresses For Contact
	 */
	@GET
	@Path("/{id}/addresses")
	@com.webcohesion.enunciate.metadata.rs.TypeHint(Address.class)
	public Response getAllAddressesByContactID(@PathParam("id") String id) 
	{
		List<Address> addresses = contactSrv.getContactById(id).getAddresses();
		return  Response.ok(addresses).build();
	}
	
	/**
	 * Validates the phone numbers of the specified {@link Contact}.
	 * 
	 * @param contact The {@link Contact} owning the phone numbers to validate
	 * 
	 * @return The {@link Contact} with the validated phone numbers
	 */
	private Contact validatePhoneNumbers(Contact contact)
	{
		Contact retVal = contact;
		
		String phoneNumber = null;
		String region = null;
		
		// TODO validate all phone numbers, not only the first one
		
		try 
		{
			phoneNumber = contact.getPhoneNumbers().get(0).getNumber(); 
		}
		catch (Exception ex) {} // ignore
		
		try
		{
			region = contact.getAddresses().get(0).getCountry(); 
		}
		catch (Exception ex) {} // ignore
		
		try
		{
			PhoneNumberValidationResult result = phoneNumberValidation.validatePhoneNumber(phoneNumber, region);
			
			if (result != null)
			{
				if (result.isValid())
				{
					phoneNumber = result.getNumber();
					contact.getPhoneNumbers().get(0).setNumber(phoneNumber);
				}
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace(System.out);
		}
					
		return retVal;
	}
}
