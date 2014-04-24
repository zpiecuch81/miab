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
 * on 2014-04-24 at 19:45:43 UTC 
 * Modify at your own risk.
 */

package pl.com.ezap.miab.messagev1endpoint.model;

/**
 * Model definition for MessageV1.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the messagev1endpoint. For a detailed explanation see:
 * <a href="http://code.google.com/p/google-http-java-client/wiki/JSON">http://code.google.com/p/google-http-java-client/wiki/JSON</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class MessageV1 extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private GeoPt deltaLocation;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long flowStamp;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Boolean flowing;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long geoIndex;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Boolean hidden;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long id;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private GeoPt location;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String message;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key @com.google.api.client.json.JsonString
  private java.lang.Long timeStamp;

  /**
   * @return value or {@code null} for none
   */
  public GeoPt getDeltaLocation() {
    return deltaLocation;
  }

  /**
   * @param deltaLocation deltaLocation or {@code null} for none
   */
  public MessageV1 setDeltaLocation(GeoPt deltaLocation) {
    this.deltaLocation = deltaLocation;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getFlowStamp() {
    return flowStamp;
  }

  /**
   * @param flowStamp flowStamp or {@code null} for none
   */
  public MessageV1 setFlowStamp(java.lang.Long flowStamp) {
    this.flowStamp = flowStamp;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Boolean getFlowing() {
    return flowing;
  }

  /**
   * @param flowing flowing or {@code null} for none
   */
  public MessageV1 setFlowing(java.lang.Boolean flowing) {
    this.flowing = flowing;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getGeoIndex() {
    return geoIndex;
  }

  /**
   * @param geoIndex geoIndex or {@code null} for none
   */
  public MessageV1 setGeoIndex(java.lang.Long geoIndex) {
    this.geoIndex = geoIndex;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Boolean getHidden() {
    return hidden;
  }

  /**
   * @param hidden hidden or {@code null} for none
   */
  public MessageV1 setHidden(java.lang.Boolean hidden) {
    this.hidden = hidden;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getId() {
    return id;
  }

  /**
   * @param id id or {@code null} for none
   */
  public MessageV1 setId(java.lang.Long id) {
    this.id = id;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public GeoPt getLocation() {
    return location;
  }

  /**
   * @param location location or {@code null} for none
   */
  public MessageV1 setLocation(GeoPt location) {
    this.location = location;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getMessage() {
    return message;
  }

  /**
   * @param message message or {@code null} for none
   */
  public MessageV1 setMessage(java.lang.String message) {
    this.message = message;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Long getTimeStamp() {
    return timeStamp;
  }

  /**
   * @param timeStamp timeStamp or {@code null} for none
   */
  public MessageV1 setTimeStamp(java.lang.Long timeStamp) {
    this.timeStamp = timeStamp;
    return this;
  }

  @Override
  public MessageV1 set(String fieldName, Object value) {
    return (MessageV1) super.set(fieldName, value);
  }

  @Override
  public MessageV1 clone() {
    return (MessageV1) super.clone();
  }

}
