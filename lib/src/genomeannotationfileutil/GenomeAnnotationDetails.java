
package genomeannotationfileutil;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * <p>Original spec-file type: GenomeAnnotationDetails</p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "genome_annotation_ref"
})
public class GenomeAnnotationDetails {

    @JsonProperty("genome_annotation_ref")
    private String genomeAnnotationRef;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("genome_annotation_ref")
    public String getGenomeAnnotationRef() {
        return genomeAnnotationRef;
    }

    @JsonProperty("genome_annotation_ref")
    public void setGenomeAnnotationRef(String genomeAnnotationRef) {
        this.genomeAnnotationRef = genomeAnnotationRef;
    }

    public GenomeAnnotationDetails withGenomeAnnotationRef(String genomeAnnotationRef) {
        this.genomeAnnotationRef = genomeAnnotationRef;
        return this;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return ((((("GenomeAnnotationDetails"+" [genomeAnnotationRef=")+ genomeAnnotationRef)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
