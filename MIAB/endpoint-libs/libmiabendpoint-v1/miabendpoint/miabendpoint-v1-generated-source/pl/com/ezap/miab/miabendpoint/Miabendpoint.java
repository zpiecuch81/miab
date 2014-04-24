/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://code.google.com/p/google-apis-client-generator/
 * (build: 2014-04-15 19:10:39 UTC)
 * on 2014-04-24 at 19:45:34 UTC 
 * Modify at your own risk.
 */

package pl.com.ezap.miab.miabendpoint;

/**
 * Service definition for Miabendpoint (v1).
 *
 * <p>
 * This is an API
 * </p>
 *
 * <p>
 * For more information about this service, see the
 * <a href="" target="_blank">API Documentation</a>
 * </p>
 *
 * <p>
 * This service uses {@link MiabendpointRequestInitializer} to initialize global parameters via its
 * {@link Builder}.
 * </p>
 *
 * @since 1.3
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public class Miabendpoint extends com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient {

  // Note: Leave this static initializer at the top of the file.
  static {
    com.google.api.client.util.Preconditions.checkState(
        com.google.api.client.googleapis.GoogleUtils.MAJOR_VERSION == 1 &&
        com.google.api.client.googleapis.GoogleUtils.MINOR_VERSION >= 15,
        "You are currently running with version %s of google-api-client. " +
        "You need at least version 1.15 of google-api-client to run version " +
        "1.16.0-rc of the miabendpoint library.", com.google.api.client.googleapis.GoogleUtils.VERSION);
  }

  /**
   * The default encoded root URL of the service. This is determined when the library is generated
   * and normally should not be changed.
   *
   * @since 1.7
   */
  public static final String DEFAULT_ROOT_URL = "https://message-in-bottle.appspot.com/_ah/api/";

  /**
   * The default encoded service path of the service. This is determined when the library is
   * generated and normally should not be changed.
   *
   * @since 1.7
   */
  public static final String DEFAULT_SERVICE_PATH = "miabendpoint/v1/";

  /**
   * The default encoded base URL of the service. This is determined when the library is generated
   * and normally should not be changed.
   */
  public static final String DEFAULT_BASE_URL = DEFAULT_ROOT_URL + DEFAULT_SERVICE_PATH;

  /**
   * Constructor.
   *
   * <p>
   * Use {@link Builder} if you need to specify any of the optional parameters.
   * </p>
   *
   * @param transport HTTP transport, which should normally be:
   *        <ul>
   *        <li>Google App Engine:
   *        {@code com.google.api.client.extensions.appengine.http.UrlFetchTransport}</li>
   *        <li>Android: {@code newCompatibleTransport} from
   *        {@code com.google.api.client.extensions.android.http.AndroidHttp}</li>
   *        <li>Java: {@link com.google.api.client.googleapis.javanet.GoogleNetHttpTransport#newTrustedTransport()}
   *        </li>
   *        </ul>
   * @param jsonFactory JSON factory, which may be:
   *        <ul>
   *        <li>Jackson: {@code com.google.api.client.json.jackson2.JacksonFactory}</li>
   *        <li>Google GSON: {@code com.google.api.client.json.gson.GsonFactory}</li>
   *        <li>Android Honeycomb or higher:
   *        {@code com.google.api.client.extensions.android.json.AndroidJsonFactory}</li>
   *        </ul>
   * @param httpRequestInitializer HTTP request initializer or {@code null} for none
   * @since 1.7
   */
  public Miabendpoint(com.google.api.client.http.HttpTransport transport, com.google.api.client.json.JsonFactory jsonFactory,
      com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
    this(new Builder(transport, jsonFactory, httpRequestInitializer));
  }

  /**
   * @param builder builder
   */
  Miabendpoint(Builder builder) {
    super(builder);
  }

  @Override
  protected void initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest<?> httpClientRequest) throws java.io.IOException {
    super.initialize(httpClientRequest);
  }

  /**
   * Create a request for the method "getMIAB".
   *
   * This request holds the parameters needed by the the miabendpoint server.  After setting any
   * optional parameters, call the {@link GetMIAB#execute()} method to invoke the remote operation.
   *
   * @param id
   * @return the request
   */
  public GetMIAB getMIAB(java.lang.Long id) throws java.io.IOException {
    GetMIAB result = new GetMIAB(id);
    initialize(result);
    return result;
  }

  public class GetMIAB extends MiabendpointRequest<pl.com.ezap.miab.miabendpoint.model.MessageV1> {

    private static final String REST_PATH = "messagev1/{id}";

    /**
     * Create a request for the method "getMIAB".
     *
     * This request holds the parameters needed by the the miabendpoint server.  After setting any
     * optional parameters, call the {@link GetMIAB#execute()} method to invoke the remote operation.
     * <p> {@link
     * GetMIAB#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)} must
     * be called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @param id
     * @since 1.13
     */
    protected GetMIAB(java.lang.Long id) {
      super(Miabendpoint.this, "GET", REST_PATH, null, pl.com.ezap.miab.miabendpoint.model.MessageV1.class);
      this.id = com.google.api.client.util.Preconditions.checkNotNull(id, "Required parameter id must be specified.");
    }

    @Override
    public com.google.api.client.http.HttpResponse executeUsingHead() throws java.io.IOException {
      return super.executeUsingHead();
    }

    @Override
    public com.google.api.client.http.HttpRequest buildHttpRequestUsingHead() throws java.io.IOException {
      return super.buildHttpRequestUsingHead();
    }

    @Override
    public GetMIAB setAlt(java.lang.String alt) {
      return (GetMIAB) super.setAlt(alt);
    }

    @Override
    public GetMIAB setFields(java.lang.String fields) {
      return (GetMIAB) super.setFields(fields);
    }

    @Override
    public GetMIAB setKey(java.lang.String key) {
      return (GetMIAB) super.setKey(key);
    }

    @Override
    public GetMIAB setOauthToken(java.lang.String oauthToken) {
      return (GetMIAB) super.setOauthToken(oauthToken);
    }

    @Override
    public GetMIAB setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (GetMIAB) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public GetMIAB setQuotaUser(java.lang.String quotaUser) {
      return (GetMIAB) super.setQuotaUser(quotaUser);
    }

    @Override
    public GetMIAB setUserIp(java.lang.String userIp) {
      return (GetMIAB) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.Long id;

    /**

     */
    public java.lang.Long getId() {
      return id;
    }

    public GetMIAB setId(java.lang.Long id) {
      this.id = id;
      return this;
    }

    @Override
    public GetMIAB set(String parameterName, Object value) {
      return (GetMIAB) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "insertMIAB".
   *
   * This request holds the parameters needed by the the miabendpoint server.  After setting any
   * optional parameters, call the {@link InsertMIAB#execute()} method to invoke the remote operation.
   *
   * @param content the {@link pl.com.ezap.miab.miabendpoint.model.MessageV1}
   * @return the request
   */
  public InsertMIAB insertMIAB(pl.com.ezap.miab.miabendpoint.model.MessageV1 content) throws java.io.IOException {
    InsertMIAB result = new InsertMIAB(content);
    initialize(result);
    return result;
  }

  public class InsertMIAB extends MiabendpointRequest<pl.com.ezap.miab.miabendpoint.model.MessageV1> {

    private static final String REST_PATH = "messagev1";

    /**
     * Create a request for the method "insertMIAB".
     *
     * This request holds the parameters needed by the the miabendpoint server.  After setting any
     * optional parameters, call the {@link InsertMIAB#execute()} method to invoke the remote
     * operation. <p> {@link
     * InsertMIAB#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)}
     * must be called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @param content the {@link pl.com.ezap.miab.miabendpoint.model.MessageV1}
     * @since 1.13
     */
    protected InsertMIAB(pl.com.ezap.miab.miabendpoint.model.MessageV1 content) {
      super(Miabendpoint.this, "POST", REST_PATH, content, pl.com.ezap.miab.miabendpoint.model.MessageV1.class);
    }

    @Override
    public InsertMIAB setAlt(java.lang.String alt) {
      return (InsertMIAB) super.setAlt(alt);
    }

    @Override
    public InsertMIAB setFields(java.lang.String fields) {
      return (InsertMIAB) super.setFields(fields);
    }

    @Override
    public InsertMIAB setKey(java.lang.String key) {
      return (InsertMIAB) super.setKey(key);
    }

    @Override
    public InsertMIAB setOauthToken(java.lang.String oauthToken) {
      return (InsertMIAB) super.setOauthToken(oauthToken);
    }

    @Override
    public InsertMIAB setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (InsertMIAB) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public InsertMIAB setQuotaUser(java.lang.String quotaUser) {
      return (InsertMIAB) super.setQuotaUser(quotaUser);
    }

    @Override
    public InsertMIAB setUserIp(java.lang.String userIp) {
      return (InsertMIAB) super.setUserIp(userIp);
    }

    @Override
    public InsertMIAB set(String parameterName, Object value) {
      return (InsertMIAB) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "listMessages".
   *
   * This request holds the parameters needed by the the miabendpoint server.  After setting any
   * optional parameters, call the {@link ListMessages#execute()} method to invoke the remote
   * operation.
   *
   * @param geoIndex
   * @param isHidden
   * @return the request
   */
  public ListMessages listMessages(java.lang.Long geoIndex, java.lang.Boolean isHidden) throws java.io.IOException {
    ListMessages result = new ListMessages(geoIndex, isHidden);
    initialize(result);
    return result;
  }

  public class ListMessages extends MiabendpointRequest<pl.com.ezap.miab.miabendpoint.model.CollectionResponseMessageV1> {

    private static final String REST_PATH = "messagev1/{geoIndex}/{isHidden}";

    /**
     * Create a request for the method "listMessages".
     *
     * This request holds the parameters needed by the the miabendpoint server.  After setting any
     * optional parameters, call the {@link ListMessages#execute()} method to invoke the remote
     * operation. <p> {@link
     * ListMessages#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)}
     * must be called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @param geoIndex
     * @param isHidden
     * @since 1.13
     */
    protected ListMessages(java.lang.Long geoIndex, java.lang.Boolean isHidden) {
      super(Miabendpoint.this, "GET", REST_PATH, null, pl.com.ezap.miab.miabendpoint.model.CollectionResponseMessageV1.class);
      this.geoIndex = com.google.api.client.util.Preconditions.checkNotNull(geoIndex, "Required parameter geoIndex must be specified.");
      this.isHidden = com.google.api.client.util.Preconditions.checkNotNull(isHidden, "Required parameter isHidden must be specified.");
    }

    @Override
    public com.google.api.client.http.HttpResponse executeUsingHead() throws java.io.IOException {
      return super.executeUsingHead();
    }

    @Override
    public com.google.api.client.http.HttpRequest buildHttpRequestUsingHead() throws java.io.IOException {
      return super.buildHttpRequestUsingHead();
    }

    @Override
    public ListMessages setAlt(java.lang.String alt) {
      return (ListMessages) super.setAlt(alt);
    }

    @Override
    public ListMessages setFields(java.lang.String fields) {
      return (ListMessages) super.setFields(fields);
    }

    @Override
    public ListMessages setKey(java.lang.String key) {
      return (ListMessages) super.setKey(key);
    }

    @Override
    public ListMessages setOauthToken(java.lang.String oauthToken) {
      return (ListMessages) super.setOauthToken(oauthToken);
    }

    @Override
    public ListMessages setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (ListMessages) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public ListMessages setQuotaUser(java.lang.String quotaUser) {
      return (ListMessages) super.setQuotaUser(quotaUser);
    }

    @Override
    public ListMessages setUserIp(java.lang.String userIp) {
      return (ListMessages) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.Long geoIndex;

    /**

     */
    public java.lang.Long getGeoIndex() {
      return geoIndex;
    }

    public ListMessages setGeoIndex(java.lang.Long geoIndex) {
      this.geoIndex = geoIndex;
      return this;
    }

    @com.google.api.client.util.Key
    private java.lang.Boolean isHidden;

    /**

     */
    public java.lang.Boolean getIsHidden() {
      return isHidden;
    }

    public ListMessages setIsHidden(java.lang.Boolean isHidden) {
      this.isHidden = isHidden;
      return this;
    }

    @com.google.api.client.util.Key
    private java.lang.String cursor;

    /**

     */
    public java.lang.String getCursor() {
      return cursor;
    }

    public ListMessages setCursor(java.lang.String cursor) {
      this.cursor = cursor;
      return this;
    }

    @com.google.api.client.util.Key
    private java.lang.Integer limit;

    /**

     */
    public java.lang.Integer getLimit() {
      return limit;
    }

    public ListMessages setLimit(java.lang.Integer limit) {
      this.limit = limit;
      return this;
    }

    @Override
    public ListMessages set(String parameterName, Object value) {
      return (ListMessages) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "removeMIAB".
   *
   * This request holds the parameters needed by the the miabendpoint server.  After setting any
   * optional parameters, call the {@link RemoveMIAB#execute()} method to invoke the remote operation.
   *
   * @param id
   * @return the request
   */
  public RemoveMIAB removeMIAB(java.lang.Long id) throws java.io.IOException {
    RemoveMIAB result = new RemoveMIAB(id);
    initialize(result);
    return result;
  }

  public class RemoveMIAB extends MiabendpointRequest<Void> {

    private static final String REST_PATH = "miab/{id}";

    /**
     * Create a request for the method "removeMIAB".
     *
     * This request holds the parameters needed by the the miabendpoint server.  After setting any
     * optional parameters, call the {@link RemoveMIAB#execute()} method to invoke the remote
     * operation. <p> {@link
     * RemoveMIAB#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)}
     * must be called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @param id
     * @since 1.13
     */
    protected RemoveMIAB(java.lang.Long id) {
      super(Miabendpoint.this, "DELETE", REST_PATH, null, Void.class);
      this.id = com.google.api.client.util.Preconditions.checkNotNull(id, "Required parameter id must be specified.");
    }

    @Override
    public RemoveMIAB setAlt(java.lang.String alt) {
      return (RemoveMIAB) super.setAlt(alt);
    }

    @Override
    public RemoveMIAB setFields(java.lang.String fields) {
      return (RemoveMIAB) super.setFields(fields);
    }

    @Override
    public RemoveMIAB setKey(java.lang.String key) {
      return (RemoveMIAB) super.setKey(key);
    }

    @Override
    public RemoveMIAB setOauthToken(java.lang.String oauthToken) {
      return (RemoveMIAB) super.setOauthToken(oauthToken);
    }

    @Override
    public RemoveMIAB setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (RemoveMIAB) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public RemoveMIAB setQuotaUser(java.lang.String quotaUser) {
      return (RemoveMIAB) super.setQuotaUser(quotaUser);
    }

    @Override
    public RemoveMIAB setUserIp(java.lang.String userIp) {
      return (RemoveMIAB) super.setUserIp(userIp);
    }

    @com.google.api.client.util.Key
    private java.lang.Long id;

    /**

     */
    public java.lang.Long getId() {
      return id;
    }

    public RemoveMIAB setId(java.lang.Long id) {
      this.id = id;
      return this;
    }

    @Override
    public RemoveMIAB set(String parameterName, Object value) {
      return (RemoveMIAB) super.set(parameterName, value);
    }
  }

  /**
   * Create a request for the method "updateMIAB".
   *
   * This request holds the parameters needed by the the miabendpoint server.  After setting any
   * optional parameters, call the {@link UpdateMIAB#execute()} method to invoke the remote operation.
   *
   * @param content the {@link pl.com.ezap.miab.miabendpoint.model.MessageV1}
   * @return the request
   */
  public UpdateMIAB updateMIAB(pl.com.ezap.miab.miabendpoint.model.MessageV1 content) throws java.io.IOException {
    UpdateMIAB result = new UpdateMIAB(content);
    initialize(result);
    return result;
  }

  public class UpdateMIAB extends MiabendpointRequest<pl.com.ezap.miab.miabendpoint.model.MessageV1> {

    private static final String REST_PATH = "messagev1";

    /**
     * Create a request for the method "updateMIAB".
     *
     * This request holds the parameters needed by the the miabendpoint server.  After setting any
     * optional parameters, call the {@link UpdateMIAB#execute()} method to invoke the remote
     * operation. <p> {@link
     * UpdateMIAB#initialize(com.google.api.client.googleapis.services.AbstractGoogleClientRequest)}
     * must be called to initialize this instance immediately after invoking the constructor. </p>
     *
     * @param content the {@link pl.com.ezap.miab.miabendpoint.model.MessageV1}
     * @since 1.13
     */
    protected UpdateMIAB(pl.com.ezap.miab.miabendpoint.model.MessageV1 content) {
      super(Miabendpoint.this, "PUT", REST_PATH, content, pl.com.ezap.miab.miabendpoint.model.MessageV1.class);
    }

    @Override
    public UpdateMIAB setAlt(java.lang.String alt) {
      return (UpdateMIAB) super.setAlt(alt);
    }

    @Override
    public UpdateMIAB setFields(java.lang.String fields) {
      return (UpdateMIAB) super.setFields(fields);
    }

    @Override
    public UpdateMIAB setKey(java.lang.String key) {
      return (UpdateMIAB) super.setKey(key);
    }

    @Override
    public UpdateMIAB setOauthToken(java.lang.String oauthToken) {
      return (UpdateMIAB) super.setOauthToken(oauthToken);
    }

    @Override
    public UpdateMIAB setPrettyPrint(java.lang.Boolean prettyPrint) {
      return (UpdateMIAB) super.setPrettyPrint(prettyPrint);
    }

    @Override
    public UpdateMIAB setQuotaUser(java.lang.String quotaUser) {
      return (UpdateMIAB) super.setQuotaUser(quotaUser);
    }

    @Override
    public UpdateMIAB setUserIp(java.lang.String userIp) {
      return (UpdateMIAB) super.setUserIp(userIp);
    }

    @Override
    public UpdateMIAB set(String parameterName, Object value) {
      return (UpdateMIAB) super.set(parameterName, value);
    }
  }

  /**
   * Builder for {@link Miabendpoint}.
   *
   * <p>
   * Implementation is not thread-safe.
   * </p>
   *
   * @since 1.3.0
   */
  public static final class Builder extends com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient.Builder {

    /**
     * Returns an instance of a new builder.
     *
     * @param transport HTTP transport, which should normally be:
     *        <ul>
     *        <li>Google App Engine:
     *        {@code com.google.api.client.extensions.appengine.http.UrlFetchTransport}</li>
     *        <li>Android: {@code newCompatibleTransport} from
     *        {@code com.google.api.client.extensions.android.http.AndroidHttp}</li>
     *        <li>Java: {@link com.google.api.client.googleapis.javanet.GoogleNetHttpTransport#newTrustedTransport()}
     *        </li>
     *        </ul>
     * @param jsonFactory JSON factory, which may be:
     *        <ul>
     *        <li>Jackson: {@code com.google.api.client.json.jackson2.JacksonFactory}</li>
     *        <li>Google GSON: {@code com.google.api.client.json.gson.GsonFactory}</li>
     *        <li>Android Honeycomb or higher:
     *        {@code com.google.api.client.extensions.android.json.AndroidJsonFactory}</li>
     *        </ul>
     * @param httpRequestInitializer HTTP request initializer or {@code null} for none
     * @since 1.7
     */
    public Builder(com.google.api.client.http.HttpTransport transport, com.google.api.client.json.JsonFactory jsonFactory,
        com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
      super(
          transport,
          jsonFactory,
          DEFAULT_ROOT_URL,
          DEFAULT_SERVICE_PATH,
          httpRequestInitializer,
          false);
    }

    /** Builds a new instance of {@link Miabendpoint}. */
    @Override
    public Miabendpoint build() {
      return new Miabendpoint(this);
    }

    @Override
    public Builder setRootUrl(String rootUrl) {
      return (Builder) super.setRootUrl(rootUrl);
    }

    @Override
    public Builder setServicePath(String servicePath) {
      return (Builder) super.setServicePath(servicePath);
    }

    @Override
    public Builder setHttpRequestInitializer(com.google.api.client.http.HttpRequestInitializer httpRequestInitializer) {
      return (Builder) super.setHttpRequestInitializer(httpRequestInitializer);
    }

    @Override
    public Builder setApplicationName(String applicationName) {
      return (Builder) super.setApplicationName(applicationName);
    }

    @Override
    public Builder setSuppressPatternChecks(boolean suppressPatternChecks) {
      return (Builder) super.setSuppressPatternChecks(suppressPatternChecks);
    }

    @Override
    public Builder setSuppressRequiredParameterChecks(boolean suppressRequiredParameterChecks) {
      return (Builder) super.setSuppressRequiredParameterChecks(suppressRequiredParameterChecks);
    }

    @Override
    public Builder setSuppressAllChecks(boolean suppressAllChecks) {
      return (Builder) super.setSuppressAllChecks(suppressAllChecks);
    }

    /**
     * Set the {@link MiabendpointRequestInitializer}.
     *
     * @since 1.12
     */
    public Builder setMiabendpointRequestInitializer(
        MiabendpointRequestInitializer miabendpointRequestInitializer) {
      return (Builder) super.setGoogleClientRequestInitializer(miabendpointRequestInitializer);
    }

    @Override
    public Builder setGoogleClientRequestInitializer(
        com.google.api.client.googleapis.services.GoogleClientRequestInitializer googleClientRequestInitializer) {
      return (Builder) super.setGoogleClientRequestInitializer(googleClientRequestInitializer);
    }
  }
}
