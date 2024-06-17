package com.eunsun.travel_mate.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.elasticsearch.support.HttpHeaders;

@Configuration
@EnableElasticsearchRepositories
public class ElasticsearchConfig extends ElasticsearchConfiguration {

  @Override
  public ClientConfiguration clientConfiguration() {
    return ClientConfiguration.builder()
        .connectedTo("localhost:9200")
        .withDefaultHeaders(defaultHeaders())
        .build();
  }

  private HttpHeaders defaultHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");
    headers.add("Accept", "application/json");
    headers.add("X-Elastic-Client-Meta", "es=7");
    headers.add("X-Elastic-Client-Meta", "xpack=true");
    headers.add("X-Elastic-Client-Meta", "flavor=default");
    headers.add("X-Elastic-Client-Meta", "lang=java");
    headers.add("X-Elastic-Client-Meta", "zone=kor");
    headers.add("X-Elastic-Client-Meta", "name=travel-mate");

    String indexSettings = """
        {
          "settings": {
            "analysis": {
              "analyzer": {
                "ngram_analyzer": {
                  "tokenizer": "ngram_tokenizer"
                }
              },
              "tokenizer": {
                "ngram_tokenizer": {
                  "type": "ngram",
                  "min_gram": 2,
                  "max_gram": 3,
                  "token_chars": [
                    "letter",
                    "digit"
                  ]
                }
              }
            }
          }
        }""";

    headers.add("X-Elastic-Create-Index-Body", indexSettings);

    return headers;
  }
}