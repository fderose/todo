package com.todo;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.todo.db.IDatabase;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Delete;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;

import javax.ws.rs.WebApplicationException;
import java.util.ArrayList;

class Searchly {
  private final JestClient jestClient;

  Searchly() {
    HttpClientConfig clientConfig = new HttpClientConfig.Builder("https://site:d7630eb6c5bc524b5a6081a2eaa919e1@bofur-us-east-1.searchly.com").multiThreaded(true).build();
    JestClientFactory factory = new JestClientFactory();
    factory.setHttpClientConfig(clientConfig);
    jestClient = factory.getObject();
  }

  void initialize() {
    try {
      jestClient.execute(new DeleteIndex.Builder("todoitems").build());
      jestClient.execute(new CreateIndex.Builder("todoitems").build());
    } catch (Exception e) {
      e.printStackTrace();
      throw new WebApplicationException(e);
    }
  }

  void addDocument(ToDoItem todoItem) {
    try {
      jestClient.execute(new Index.Builder(todoItem).index("todoitems").type("todoitem").id(String.valueOf(todoItem.getId())).build());
    } catch (Exception e) {
      e.printStackTrace();
      throw new WebApplicationException(e);
    }
  }

  ArrayList<ScoredToDoItem> search(String term, double boost, IDatabase<ToDoItem> db) {
    String query = String.format("{\n" +
      "    \"query\": {\n" +
      "        \"filtered\" : {\n" +
      "            \"query\" : {\n" +
      "                \"query_string\" : {\n" +
      "                    \"fields\" : [\"title^%.1f\", \"body\"],\n" +
      "                    \"query\" : \"%s\"\n" +
      "                }\n" +
      "            }\n" +
      "        }\n" +
      "    }\n" +
      "}", boost, term);
    Search search = new Search.Builder(query).build();
    JestResult result = null;
    try {
      result = jestClient.execute(search);
    } catch (Exception e) {
      e.printStackTrace();
      throw new WebApplicationException(e);
    }
    JsonObject jsonObject = result.getJsonObject();
    ArrayList<ScoredToDoItem> arrayList = new ArrayList<>();
    int numHits = jsonObject.get("hits").getAsJsonObject().get("total").getAsInt();
    if (numHits > 0) {
      JsonArray jsonArray = jsonObject.get("hits").getAsJsonObject().get("hits").getAsJsonArray();
      for (int i = 0; i < numHits; i++) {
        JsonObject hit = jsonArray.get(i).getAsJsonObject().get("_source").getAsJsonObject();
        ToDoItem todoItem = db.getById(hit.get("id").getAsInt());
        arrayList.add(new ScoredToDoItem(todoItem, jsonArray.get(i).getAsJsonObject().get("_score").getAsDouble()));
      }
    }
    return arrayList;
  }

  void deleteDocument(int id) {
    try {
      jestClient.execute(new Delete.Builder(String.valueOf(id)).index("todoitems").type("todoitem").build());
    } catch (Exception e) {
      throw new WebApplicationException(e);
    }
  }
}
