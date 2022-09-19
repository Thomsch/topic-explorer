import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.PathFilterGroup;
import org.eclipse.jgit.treewalk.filter.PathSuffixFilter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class App {
    public static void main(String[] args) {

//        try {
//            Files.walk(Paths.get("/Users/thomas/Workplace/netbeans"))
//                    .filter(Files::isRegularFile)
//                    .filter(path -> path.getFileName().toString().endsWith(".java"))
//                    .findFirst().ifPresent(App::getIdentifiers);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }


        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        repositoryBuilder.findGitDir(Paths.get("/Users/thomas/Workplace/repositories/metric-history").toFile());
        repositoryBuilder.readEnvironment();
        Repository repository = null;
        try {
            repository = repositoryBuilder.build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Git git = Git.wrap(repository);

//      // Read specific file in specific commit

        try {
            String treeName = "refs/heads/main"; // tag or branch

            LogCommand logCommand = git.log();
            ObjectId objectId = repository.resolve(Constants.HEAD); // TODO: Use the ref head of a specific branch or for each branch rather
            logCommand.add(objectId);
            for (RevCommit commit : logCommand.call()) {
                System.out.println("=== " + commit.getName().substring(0,7) + " === " + commit.getAuthorIdent().getWhen());
                System.out.println("-> " + commit.getShortMessage());

                RevTree tree = commit.getTree();

                TreeWalk treeWalk = new TreeWalk(repository);
                treeWalk.setFilter(PathSuffixFilter.create(".java"));

                treeWalk.addTree(tree);
                treeWalk.setRecursive(true);
                while (treeWalk.next()) {
                    System.out.println("------------------");
//                    System.out.println(treeWalk.getPathString());
//
                    ObjectId objectId1 = treeWalk.getObjectId(0);
                    ObjectLoader loader = repository.open(objectId1);

                    CompilationUnit compilationUnit = null;
                    compilationUnit = StaticJavaParser.parse(loader.openStream());

                    List<ClassOrInterfaceDeclaration> classes = compilationUnit.findAll(ClassOrInterfaceDeclaration.class);;

                    for(ClassOrInterfaceDeclaration clasz: classes) {
                        System.out.println(clasz.getFullyQualifiedName().get());
                    }
                }
            }
        } catch (NoHeadException e) {
            throw new RuntimeException(e);
        } catch (GitAPIException e) {
            throw new RuntimeException(e);
        } catch (AmbiguousObjectException e) {
            throw new RuntimeException(e);
        } catch (IncorrectObjectTypeException e) {
            throw new RuntimeException(e);
        } catch (MissingObjectException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //
//        for (RevCommit commit : commits) {
//            boolean foundInThisBranch = false;
//
//            RevCommit targetCommit = walk.parseCommit(repository.resolve(commit.getName()));
//
//
//            for (Map.Entry<String, Ref> e : repo.getAllRefs().entrySet()) {
//                if (e.getKey().startsWith(Constants.R_HEADS)) {
//                    if (walk.isMergedInto(targetCommit, walk.parseCommit(
//                            e.getValue().getObjectId()))) {
//                        String foundInBranch = e.getValue().getName();
//                        if (branchName.equals(foundInBranch)) {
//                            foundInThisBranch = true;
//                            break;
//                        }
//                    }
//                }
//            }
//
//            if (foundInThisBranch) {
//                System.out.println(commit.getName());
//                System.out.println(commit.getAuthorIdent().getName());
//                System.out.println(new Date(commit.getCommitTime() * 1000L));
//                System.out.println(commit.getFullMessage());
//            }
//
//        RevWalk walk = new RevWalk(git.getRepository());
//        walk.setRetainBody(false);
//
//        walk.markStart(walk.parseCommit(repository.resolve(Constants.HEAD)));
//
//        for (RevCommit commit : walk) {
//            System.out.println(commit);
//
//            commit.disposeBody();
//        }

        // Change revision

        // Get files in revision

        // Parse
        /*CompilationUnit compilationUnit = StaticJavaParser.parse("class A {}");
        Optional<ClassOrInterfaceDeclaration> classA = compilationUnit.getClassByName("A");

        compilationUnit.findAll(FieldDeclaration.class).stream();
*/        // Just class names
    }

    private static void getIdentifiers(Path path) {
        CompilationUnit compilationUnit = null;

        try {
            compilationUnit = StaticJavaParser.parse(path);

            List<ClassOrInterfaceDeclaration> classes = compilationUnit.findAll(ClassOrInterfaceDeclaration.class);
            System.out.println(classes.size());

//            compilationUnit.walk(new Consumer<Node>() {
//                @Override
//                public void accept(Node node) {
//                    if(node instanceof /...)
//                }
//            });

            for(ClassOrInterfaceDeclaration clasz: classes) {
                System.out.println(clasz.getFullyQualifiedName().toString());

            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
