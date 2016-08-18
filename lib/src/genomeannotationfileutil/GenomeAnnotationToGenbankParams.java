
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
 * <p>Original spec-file type: GenomeAnnotationToGenbankParams</p>
 * <pre>
 * genome_ref -- Reference to the GenomeAnnotation or Genome object in KBase in 
 *               any ws supported format
 * OR
 * genome_name + workspace_name -- specifiy the genome name and workspace name
 *               of what you want.  If genome_ref is defined, these args are ignored.
 * new_genbank_file_name -- specify the output name of the genbank file, optional
 * save_to_shock -- set to 1 or 0, if 1 then output is saved to shock. default is zero
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "genome_ref",
    "genome_name",
    "workspace_name",
    "new_genbank_file_name",
    "save_to_shock"
})
public class GenomeAnnotationToGenbankParams {

    @JsonProperty("genome_ref")
    private String genomeRef;
    @JsonProperty("genome_name")
    private String genomeName;
    @JsonProperty("workspace_name")
    private String workspaceName;
    @JsonProperty("new_genbank_file_name")
    private String newGenbankFileName;
    @JsonProperty("save_to_shock")
    private Long saveToShock;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("genome_ref")
    public String getGenomeRef() {
        return genomeRef;
    }

    @JsonProperty("genome_ref")
    public void setGenomeRef(String genomeRef) {
        this.genomeRef = genomeRef;
    }

    public GenomeAnnotationToGenbankParams withGenomeRef(String genomeRef) {
        this.genomeRef = genomeRef;
        return this;
    }

    @JsonProperty("genome_name")
    public String getGenomeName() {
        return genomeName;
    }

    @JsonProperty("genome_name")
    public void setGenomeName(String genomeName) {
        this.genomeName = genomeName;
    }

    public GenomeAnnotationToGenbankParams withGenomeName(String genomeName) {
        this.genomeName = genomeName;
        return this;
    }

    @JsonProperty("workspace_name")
    public String getWorkspaceName() {
        return workspaceName;
    }

    @JsonProperty("workspace_name")
    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    public GenomeAnnotationToGenbankParams withWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
        return this;
    }

    @JsonProperty("new_genbank_file_name")
    public String getNewGenbankFileName() {
        return newGenbankFileName;
    }

    @JsonProperty("new_genbank_file_name")
    public void setNewGenbankFileName(String newGenbankFileName) {
        this.newGenbankFileName = newGenbankFileName;
    }

    public GenomeAnnotationToGenbankParams withNewGenbankFileName(String newGenbankFileName) {
        this.newGenbankFileName = newGenbankFileName;
        return this;
    }

    @JsonProperty("save_to_shock")
    public Long getSaveToShock() {
        return saveToShock;
    }

    @JsonProperty("save_to_shock")
    public void setSaveToShock(Long saveToShock) {
        this.saveToShock = saveToShock;
    }

    public GenomeAnnotationToGenbankParams withSaveToShock(Long saveToShock) {
        this.saveToShock = saveToShock;
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
        return ((((((((((((("GenomeAnnotationToGenbankParams"+" [genomeRef=")+ genomeRef)+", genomeName=")+ genomeName)+", workspaceName=")+ workspaceName)+", newGenbankFileName=")+ newGenbankFileName)+", saveToShock=")+ saveToShock)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
