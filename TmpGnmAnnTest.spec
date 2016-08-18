/*
A KBase module: TmpGnmAnnTest
*/

module TmpGnmAnnTest {

    typedef structure {
        string output_workspace_name;
        string output_object_name;
        string genome_name;
        mapping<string,string> protein_id_to_sequence;
    } PrepareTestGenomeAnnotationFromProteinsParams;

    funcdef prepare_test_genome_annotation_from_proteins(
        PrepareTestGenomeAnnotationFromProteinsParams params) 
        returns (string ga_ref) authentication required;

};
