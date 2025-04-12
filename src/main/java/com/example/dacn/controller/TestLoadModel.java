package com.example.dacn.controller;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import java.io.File;

public class TestLoadModel {
    public static void main(String[] args) {
        System.out.println("Đang load mô hình...");
        Word2Vec vec = WordVectorSerializer.readWord2VecModel(new File("src/main/resources/models/GoogleNews-vectors-negative300.bin.gz"));
        System.out.println("Load thành công");
        System.out.println("Vector của 'java': " + vec.getWordVectorMatrix("java"));
    }
}
