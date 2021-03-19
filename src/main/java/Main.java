import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.Statement;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.github.javaparser.StaticJavaParser.parse;

public class Main {

    static String projectId, baseFolderPath, outputFilePath;
    static List<ProjectFile> projectFiles = new ArrayList<>();
    public static void main(String[] args) {
        if(args.length != 1)
        {
            System.out.println("Error: incorrect program invocation. Correct: control_stmt_counter.jar config/config.txt");
            return;
        }
        else
        {
            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader(args[0]));
                projectId = reader.readLine().substring(10);
                baseFolderPath = reader.readLine().substring(15);
                reader.close();

            } catch (FileNotFoundException e) {
                System.out.println("The configuration file path does not exist or it is incorrect.");
                return;
            }
            catch (IOException e){
                e.printStackTrace();
                return;
            }

            outputFilePath = projectId + "_control_stmt_count.json";

            try {
                File results = new File(outputFilePath);
                FileWriter myWriter = new FileWriter(results);
                myWriter.write("test");
                myWriter.close();

            } catch (NoSuchFileException|FileNotFoundException e) {
                System.out.println("The results file path does not exist or it is incorrect.");
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                int start_path = baseFolderPath.lastIndexOf('\\')+1;
                int project_name_length = baseFolderPath.substring(start_path).length();
                Files.walk(Paths.get(baseFolderPath))
                        .filter(Files::isRegularFile)
                        .filter(f -> f.getFileName().toString().endsWith(".java"))
                        .forEach(file -> {
                            nr_if_stmt = 0;
                            nr_while_stmt = 0;
                            nr_for_stmt = 0;
                            nr_do_stmt = 0;
                            nr_try_stmt = 0;
                            try {
                                CompilationUnit cUnit = parse(new File(file.toString()));
                                parc(cUnit);
                                String relative_file_path = file.toString().substring(start_path + project_name_length + 1);
                                if(nr_if_stmt>0)
                                    projectFiles.add(new ProjectFile("if", "Control Statements", relative_file_path.replace("\\","/"), nr_if_stmt));
                                if(nr_while_stmt>0)
                                    projectFiles.add(new ProjectFile("while", "Control Statements", relative_file_path.replace("\\","/"), nr_while_stmt));
                                if(nr_for_stmt>0)
                                    projectFiles.add(new ProjectFile("for", "Control Statements", relative_file_path.replace("\\","/"), nr_for_stmt));
                                if(nr_do_stmt>0)
                                    projectFiles.add(new ProjectFile("do", "Control Statements", relative_file_path.replace("\\","/"), nr_do_stmt));
                                if(nr_try_stmt>0)
                                    projectFiles.add(new ProjectFile("try", "Control Statements", relative_file_path.replace("\\","/"), nr_try_stmt));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }

                        });
                ObjectMapper objectMapper = new ObjectMapper();
                String projectFiles_json_string = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(projectFiles);

                try {
                    File results = new File(outputFilePath);
                    FileWriter myWriter = new FileWriter(results);
                    myWriter.write(projectFiles_json_string);
                    myWriter.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
            } catch (NoSuchFileException e){
                System.out.println("The project path does not exist or it is incorrect.");
                return;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("The analysis completed successfully.");
        }

    }

    static int nr_if_stmt;
    static int nr_while_stmt;
    static int nr_for_stmt;
    static int nr_do_stmt;
    static int nr_try_stmt;

    public static void parc(Node node)
    {
        if (node instanceof Statement){
            if(((Statement) node).isIfStmt())
                nr_if_stmt++;
            if(((Statement) node).isWhileStmt())
                nr_while_stmt++;
            if(((Statement) node).isForStmt() || ((Statement) node).isForEachStmt())
                nr_for_stmt++;
            if(((Statement) node).isDoStmt())
                nr_do_stmt++;
            if(((Statement) node).isTryStmt())
                nr_try_stmt++;
        }
        for (Node child : node.getChildNodes()) {
            parc(child);
        }
    }
}

