package io.narayana.nta.restapi.helpers;

import io.narayana.nta.restapi.models.URIConstants;

import javax.ws.rs.core.Link;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created with IntelliJ IDEA.
 * User: Amila
 * Date: 14/05/14
 * Time: 00:14
 * To change this template use File | Settings | File Templates.
 */
public final class LinkBuilder
{
    public static final Link participantRecordLinkBuilder(Long id)
    {
        try
        {
            Link.Builder builder = Link.fromUri(new URI(URIConstants.NTA_URI+URIConstants.RootURI+URIConstants.ParticipantRecordURI+"/"+id));
            Link link = builder.build();
            return link;
        }
        catch (URISyntaxException e)
        {
            return null;
        }
    }

    public static final Link eventLinkBuilder(Long id)
    {
        try
        {
            Link.Builder builder = Link.fromUri(new URI(URIConstants.NTA_URI+URIConstants.RootURI+URIConstants.EventURI+"/"+id));
            Link link = builder.build();
            return link;
        }
        catch (URISyntaxException e)
        {
            return  null;
        }
    }

    public static final Link transactionLinkBuilder(Long id)
    {
        try
        {
            Link.Builder builder = Link.fromUri(new URI(URIConstants.NTA_URI+URIConstants.RootURI+URIConstants.TransactionURI+"/"+id));
            Link link = builder.build();
            return link;
        }
        catch (URISyntaxException e)
        {
            return  null;
        }
    }
}
