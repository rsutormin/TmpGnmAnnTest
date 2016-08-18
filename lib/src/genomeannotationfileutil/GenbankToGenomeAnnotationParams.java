
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
 * <p>Original spec-file type: GenbankToGenomeAnnotationParams</p>
 * <pre>
 * file_path or shock_id -- Local path or shock_id of the uploaded file with genome
 *                sequence in GenBank format or zip-file with GenBank files.
 * genome_name -- The name you would like to use to reference this GenomeAnnotation.  
 *                If not supplied, will use the Taxon Id and the data source to 
 *                determine the name.
 * taxon_wsname - name of the workspace containing the Taxonomy data, defaults to 'ReferenceTaxons'
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "file_path",
    "shock_id",
    "ftp_url",
    "genome_name",
    "workspace_name",
    "source",
    "taxon_wsname",
    "convert_to_legacy"
})
public class GenbankToGenomeAnnotationParams {

    @JsonProperty("file_path")
    private String filePath;
    @JsonProperty("shock_id")
    private String shockId;
    @JsonProperty("ftp_url")
    private String ftpUrl;
    @JsonProperty("genome_name")
    private String genomeName;
    @JsonProperty("workspace_name")
    private String workspaceName;
    @JsonProperty("source")
    private String source;
    @JsonProperty("taxon_wsname")
    private String taxonWsname;
    @JsonProperty("convert_to_legacy")
    private Long convertToLegacy;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("file_path")
    public String getFilePath() {
        return filePath;
    }

    @JsonProperty("file_path")
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public GenbankToGenomeAnnotationParams withFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    @JsonProperty("shock_id")
    public String getShockId() {
        return shockId;
    }

    @JsonProperty("shock_id")
    public void setShockId(String shockId) {
        this.shockId = shockId;
    }

    public GenbankToGenomeAnnotationParams withShockId(String shockId) {
        this.shockId = shockId;
        return this;
    }

    @JsonProperty("ftp_url")
    public String getFtpUrl() {
        return ftpUrl;
    }

    @JsonProperty("ftp_url")
    public void setFtpUrl(String ftpUrl) {
        this.ftpUrl = ftpUrl;
    }

    public GenbankToGenomeAnnotationParams withFtpUrl(String ftpUrl) {
        this.ftpUrl = ftpUrl;
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

    public GenbankToGenomeAnnotationParams withGenomeName(String genomeName) {
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

    public GenbankToGenomeAnnotationParams withWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
        return this;
    }

    @JsonProperty("source")
    public String getSource() {
        return source;
    }

    @JsonProperty("source")
    public void setSource(String source) {
        this.source = source;
    }

    public GenbankToGenomeAnnotationParams withSource(String source) {
        this.source = source;
        return this;
    }

    @JsonProperty("taxon_wsname")
    public String getTaxonWsname() {
        return taxonWsname;
    }

    @JsonProperty("taxon_wsname")
    public void setTaxonWsname(String taxonWsname) {
        this.taxonWsname = taxonWsname;
    }

    public GenbankToGenomeAnnotationParams withTaxonWsname(String taxonWsname) {
        this.taxonWsname = taxonWsname;
        return this;
    }

    @JsonProperty("convert_to_legacy")
    public Long getConvertToLegacy() {
        return convertToLegacy;
    }

    @JsonProperty("convert_to_legacy")
    public void setConvertToLegacy(Long convertToLegacy) {
        this.convertToLegacy = convertToLegacy;
    }

    public GenbankToGenomeAnnotationParams withConvertToLegacy(Long convertToLegacy) {
        this.convertToLegacy = convertToLegacy;
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
        return ((((((((((((((((((("GenbankToGenomeAnnotationParams"+" [filePath=")+ filePath)+", shockId=")+ shockId)+", ftpUrl=")+ ftpUrl)+", genomeName=")+ genomeName)+", workspaceName=")+ workspaceName)+", source=")+ source)+", taxonWsname=")+ taxonWsname)+", convertToLegacy=")+ convertToLegacy)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
