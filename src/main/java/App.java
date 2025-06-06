import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import io.github.cdimascio.dotenv.Dotenv;
import org.bson.Document;

import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        // Carrega as variáveis do arquivo .env
        // .load() procura o .env na raiz do projeto
        Dotenv dotenv = Dotenv.load();

        // Obtem os valores da variáveis do .env
        String connectionURI = dotenv.get("MONGO_URI");
        String dbName = dotenv.get("DATABASE_NAME");
        String collectionName = dotenv.get("COLLECTION_NAME");

        MongoClient mongoClient = null;

        try{

            if (connectionURI == null || connectionURI.isEmpty()){
                System.out.println("Connection to MongoDB failed");
            }else if (dbName == null || dbName.isEmpty()){
                System.out.println("Database name not found");
            }else if (collectionName == null || collectionName.isEmpty()){
                System.out.println("Collection name not found");
            }

            System.out.println("Connecting to MongoDB...");
            mongoClient = MongoClients.create(connectionURI);
            System.out.println("Connected to MongoDB");

            System.out.println("Creating collection...");
            MongoDatabase database = mongoClient.getDatabase(dbName);
            System.out.println("Connected to database");

            System.out.println("Creating collection...");
            MongoCollection<Document> clientsData = database.getCollection(collectionName);
            System.out.println("Connected to collection");

        boolean conectado = true;
        while (conectado){
            System.out.println("\n1. Insert new client");
            System.out.println("2. Delete client");
            System.out.println("3. Update client");
            System.out.println("4. Find client");
            System.out.println("5. Find client by id");
            System.out.println("6. Close");
            System.out.println("Enter your choice: ");

            String choice = in.next();
            switch (choice){
                case "1":
                    boolean inputValido = false;
                    while (!inputValido) {
                        try {
                            System.out.println("ID: ");
                            int id = in.nextInt();
                            System.out.println("Name: ");
                            String name = in.next();
                            Document query = new Document("_id", id).append("name", name);
                            InsertOneResult result = clientsData.insertOne(query);

                            System.out.println("Documento inserido com sucesso! ID: " + result.getInsertedId());
                            inputValido = true; // Se a inserção foi bem-sucedida, define a flag como true para sair do loop

                            } catch (Exception e) {
                                // O erro de duplicidade de chave no MongoDB.
                                if (e.getMessage() != null) {
                                    System.out.println("Erro:Por favor, digite novamente");
                                    // AQUI: inputValido permanece 'false', então o loop 'while' continuará e pedirá um novo ID.
                                }
                            }
                        }
                        break;
                case "2":
                    boolean inputDelete = false;
                    while (!inputDelete){
                        try {
                            System.out.println("Delete client by id: ");
                            int userDeletionId = in.nextInt();
                            DeleteResult deleteUser = clientsData.deleteOne(Filters.eq("_id", userDeletionId ));
                            inputDelete = true;

                        } catch (Exception e) {
                            if (e.getMessage() != null) {
                                System.out.println(e.getMessage());
                                System.out.println("Erro:Por favor, digite novamente");
                            }
                         }
                    }

                case "5":
                    System.out.println("Search client by id: ");
                    int searchId = in.nextInt();
                    Document user = clientsData.find(Filters.eq("_id", searchId)).first();
                    System.out.println("Name: " + user.toJson());
                    break;

                case "6":
                    conectado = false;
            }
        }
        in.close();



        }catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        } finally {
            if (mongoClient != null){
                mongoClient.close();
            }
        }


    }
}
