package tmpgnmanntest;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import us.kbase.auth.AuthToken;
import us.kbase.common.service.JsonServerMethod;
import us.kbase.common.service.JsonServerServlet;
import us.kbase.common.service.JsonServerSyslog;
import us.kbase.common.service.RpcContext;

//BEGIN_HEADER
import genomeannotationfileutil.GenbankToGenomeAnnotationParams;
import genomeannotationfileutil.GenomeAnnotationDetails;
import genomeannotationfileutil.GenomeAnnotationFileUtilClient;

import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import us.kbase.common.service.Tuple4;
import us.kbase.genbank.GenometoGbk;
import us.kbase.kbasegenomes.Feature;
import us.kbase.kbasegenomes.Genome;
//END_HEADER

/**
 * <p>Original spec-file module name: TmpGnmAnnTest</p>
 * <pre>
 * A KBase module: TmpGnmAnnTest
 * </pre>
 */
public class TmpGnmAnnTestServer extends JsonServerServlet {
    private static final long serialVersionUID = 1L;
    private static final String version = "0.0.1";
    private static final String gitUrl = "";
    private static final String gitCommitHash = "";

    //BEGIN_CLASS_HEADER
    private File scratchDir = null;
    //END_CLASS_HEADER

    public TmpGnmAnnTestServer() throws Exception {
        super("TmpGnmAnnTest");
        //BEGIN_CONSTRUCTOR
        scratchDir = new File(config.get("scratch"));
        //END_CONSTRUCTOR
    }

    /**
     * <p>Original spec-file function name: prepare_test_genome_annotation_from_proteins</p>
     * <pre>
     * </pre>
     * @param   params   instance of type {@link tmpgnmanntest.PrepareTestGenomeAnnotationFromProteinsParams PrepareTestGenomeAnnotationFromProteinsParams}
     * @return   parameter "ga_ref" of String
     */
    @JsonServerMethod(rpc = "TmpGnmAnnTest.prepare_test_genome_annotation_from_proteins", async=true)
    public String prepareTestGenomeAnnotationFromProteins(PrepareTestGenomeAnnotationFromProteinsParams params, AuthToken authPart, RpcContext jsonRpcContext) throws Exception {
        String returnVal = null;
        //BEGIN prepare_test_genome_annotation_from_proteins
        File tempDir = Files.createTempDirectory(scratchDir.toPath(), "gbk_").toFile();
        try {
            File gbkFile = new File(tempDir, "genome.gbk");
            List<Feature> features = new ArrayList<>();
            List<String> proteinIds = new ArrayList<>(params.getProteinIdToSequence().keySet());
            String contigId = "contig1";
            long featureStart = 1;
            for (int genePos = 0; genePos < proteinIds.size(); genePos++) {
                String proteinId = proteinIds.get(genePos);
                String proteinSeq = params.getProteinIdToSequence().get(proteinId);
                long featureLength = proteinSeq.length() * 3;
                features.add(new Feature().withId(proteinId)
                        .withProteinTranslation(proteinSeq)
                        .withType("CDS").withLocation(Arrays.asList(
                                new Tuple4<String, Long, String, Long>().withE1(contigId)
                                .withE2(featureStart).withE3("+").withE4(featureLength))));
                featureStart += featureLength;
            }
            char[] contigSeqChars = new char[(int)featureStart + 5];
            Arrays.fill(contigSeqChars, 'a');
            Genome genome = new Genome().withScientificName(params.getGenomeName())
                    .withFeatures(features).withTaxonomy("Bacteria; Proteobacteria; Gammaproteobacteria; " +
                    		"Alteromonadales; Shewanellaceae; Shewanella.");
            GenometoGbk.writeGenbankFile(genome, contigId, contigId, new String(contigSeqChars), 
                    gbkFile);
            GenomeAnnotationFileUtilClient gafu = new GenomeAnnotationFileUtilClient(
                    new URL(System.getenv("SDK_CALLBACK_URL")), authPart);
            gafu.setIsInsecureHttpConnectionAllowed(true);
            GenomeAnnotationDetails ret = gafu.genbankToGenomeAnnotation(
                    new GenbankToGenomeAnnotationParams().withFilePath(
                    gbkFile.getCanonicalPath()).withGenomeName(params.getOutputObjectName())
                    .withWorkspaceName(params.getOutputWorkspaceName()));
            returnVal = ret.getGenomeAnnotationRef();
        } finally {
            FileUtils.deleteQuietly(tempDir);
        }
        //END prepare_test_genome_annotation_from_proteins
        return returnVal;
    }
    @JsonServerMethod(rpc = "TmpGnmAnnTest.status")
    public Map<String, Object> status() {
        Map<String, Object> returnVal = null;
        //BEGIN_STATUS
        returnVal = new LinkedHashMap<String, Object>();
        returnVal.put("state", "OK");
        returnVal.put("message", "");
        returnVal.put("version", version);
        returnVal.put("git_url", gitUrl);
        returnVal.put("git_commit_hash", gitCommitHash);
        //END_STATUS
        return returnVal;
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 1) {
            new TmpGnmAnnTestServer().startupServer(Integer.parseInt(args[0]));
        } else if (args.length == 3) {
            JsonServerSyslog.setStaticUseSyslog(false);
            JsonServerSyslog.setStaticMlogFile(args[1] + ".log");
            new TmpGnmAnnTestServer().processRpcCall(new File(args[0]), new File(args[1]), args[2]);
        } else {
            System.out.println("Usage: <program> <server_port>");
            System.out.println("   or: <program> <context_json_file> <output_json_file> <token>");
            return;
        }
    }
}
