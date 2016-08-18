package tmpgnmanntest.test;

import genomeannotationapi.FeatureData;
import genomeannotationapi.GenomeAnnotationAPIClient;
import genomeannotationapi.GenomeAnnotationData;
import genomeannotationapi.GetCombinedDataParams;
import genomeannotationapi.ProteinData;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.ini4j.Ini;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import tmpgnmanntest.PrepareTestGenomeAnnotationFromProteinsParams;
import tmpgnmanntest.TmpGnmAnnTestServer;
import us.kbase.auth.AuthToken;
import us.kbase.auth.AuthService;
import us.kbase.common.service.JsonServerSyslog;
import us.kbase.common.service.RpcContext;
import us.kbase.common.service.UObject;
import us.kbase.common.utils.FastaReader;
import us.kbase.workspace.CreateWorkspaceParams;
import us.kbase.workspace.ProvenanceAction;
import us.kbase.workspace.WorkspaceClient;
import us.kbase.workspace.WorkspaceIdentity;

public class TmpGnmAnnTestServerTest {
    private static AuthToken token = null;
    private static Map<String, String> config = null;
    private static WorkspaceClient wsClient = null;
    private static String wsName = null;
    private static TmpGnmAnnTestServer impl = null;
    
    @BeforeClass
    public static void init() throws Exception {
        //TODO AUTH make configurable?
        token = AuthService.validateToken(System.getenv("KB_AUTH_TOKEN"));
        String configFilePath = System.getenv("KB_DEPLOYMENT_CONFIG");
        File deploy = new File(configFilePath);
        Ini ini = new Ini(deploy);
        config = ini.get("TmpGnmAnnTest");
        wsClient = new WorkspaceClient(new URL(config.get("workspace-url")), token);
        wsClient.setIsInsecureHttpConnectionAllowed(true);
        // These lines are necessary because we don't want to start linux syslog bridge service
        JsonServerSyslog.setStaticUseSyslog(false);
        JsonServerSyslog.setStaticMlogFile(new File(config.get("scratch"), "test.log").getAbsolutePath());
        impl = new TmpGnmAnnTestServer();
    }
    
    private static String getWsName() throws Exception {
        if (wsName == null) {
            long suffix = System.currentTimeMillis();
            wsName = "test_TmpGnmAnnTest_" + suffix;
            wsClient.createWorkspace(new CreateWorkspaceParams().withWorkspace(wsName));
        }
        return wsName;
    }
    
    private static RpcContext getContext() {
        return new RpcContext().withProvenance(Arrays.asList(new ProvenanceAction()
            .withService("TmpGnmAnnTest").withMethod("please_never_use_it_in_production")
            .withMethodParams(new ArrayList<UObject>())));
    }
    
    @AfterClass
    public static void cleanup() {
        if (wsName != null) {
            try {
                wsClient.deleteWorkspace(new WorkspaceIdentity().withWorkspace(wsName));
                System.out.println("Test workspace was deleted");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    @Test
    public void testPrepareTestGenomeAnnotationFromProteins() throws Exception {
        // wsClient.saveObjects(new SaveObjectsParams().withWorkspace(getWsName()).withObjects(Arrays.asList(
        //         new ObjectSaveData().withType("SomeModule.SomeType").withName(objName).withData(new UObject(obj)))));
        FastaReader fr = new FastaReader(new File("/kb/module/test/data/Shewanella_ANA_3_uid58347.fasta"));
        Map<String, String> proteinIdToSeq = fr.readAll();
        fr.close();
        //List<String> proteinIds = new ArrayList<>(proteinIdToSeq.keySet());
        Map<String, String> proteinIdToSequence = new LinkedHashMap<>();
        /*for (int i = 0; i < 10; i++) {
            String proteinId = proteinIds.get(i);
            proteinIdToSequence.put(proteinId, proteinIdToSeq.get(proteinId));
        }*/
        proteinIdToSequence.putAll(proteinIdToSeq);
        int proteinCount = proteinIdToSequence.size();
        String gaRef = impl.prepareTestGenomeAnnotationFromProteins(
                new PrepareTestGenomeAnnotationFromProteinsParams().withOutputWorkspaceName(getWsName())
                .withOutputObjectName("genome.1").withGenomeName("Shewanella ANA 3 uid58347")
                .withProteinIdToSequence(proteinIdToSequence), token, getContext());
        GenomeAnnotationAPIClient gaapi = new GenomeAnnotationAPIClient(new URL(System.getenv("SDK_CALLBACK_URL")), token);
        gaapi.setIsInsecureHttpConnectionAllowed(true);
        GenomeAnnotationData gad = gaapi.getCombinedData(new GetCombinedDataParams().withRef(gaRef));
        Assert.assertEquals(proteinCount, gad.getFeatureByIdByType().get(gad.getGeneType()).size());
        Assert.assertEquals(proteinCount, gad.getFeatureByIdByType().get(gad.getCdsType()).size());
        Assert.assertEquals(proteinCount, gad.getProteinByCdsId().size());
        Assert.assertEquals(proteinCount, gad.getCdsIdsByGeneId().size());
        //System.out.println("Summary: " + gad.getSummary());
        Map<String, FeatureData> genes = gad.getFeatureByIdByType().get(gad.getGeneType());
        Map<String, FeatureData> cdss = gad.getFeatureByIdByType().get(gad.getCdsType());
        Map<String, List<String>> geneIdToCdsId = gad.getCdsIdsByGeneId();
        Map<String, ProteinData> cdsIdToProt = gad.getProteinByCdsId();
        int genesFound = 0;
        for (String geneId : genes.keySet()) {
            for (String cdsId : geneIdToCdsId.get(geneId)) {
                FeatureData cds = cdss.get(cdsId);
                ProteinData prot = cdsIdToProt.get(cdsId);
                String gaSeq = prot.getProteinAminoAcidSequence();
                for (String key : cds.getFeatureAliases().keySet()) {
                    if (proteinIdToSequence.containsKey(key)) {
                        String origSeq = proteinIdToSequence.get(key);
                        boolean match = gaSeq.equals(origSeq);
                        //System.out.println("Found " + key + " (" + geneId + ", " + cdsId + "): " + match);
                        if (match)
                            genesFound++;
                    }
                }
            }
        }
        Assert.assertEquals(proteinCount, genesFound);
    }
}
