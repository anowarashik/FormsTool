import oracle.forms.jdapi.*;

import java.io.File;
import java.io.FilenameFilter;

import java.util.List;
import java.util.ArrayList;
import java.util.Locale;


/**
 * ================================================================
 * ORACLE FORMS WIDTH TOOL
 * ================================================================
 *
 * This tool changes ONLY the FMB file.
 *
 * 1. Target Forms Items
 *       Maximum Length < 30
 *       -> 30
 *
 * 2. Record Group Columns
 *       Maximum Length < 30
 *       -> 30
 *
 * 3. Program Unit Variables
 *       VARCHAR2(5)
 *       -> VARCHAR2(30)
 *
 * 4. ALL FORM TRIGGERS
 *       VARCHAR2(5)
 *       -> VARCHAR2(30)
 *
 *
 * IMPORTANT:
 *
 * DATABASE TABLE / DATABASE COLUMN IS NEVER CHANGED.
 *
 * No database connection.
 * No ALTER TABLE.
 * No SQL execution.
 *
 * ================================================================
 */


public class FormsWidthTool {


    // ============================================================
    // CONFIGURATION
    // ============================================================

    private static final String FMB_FOLDER =
        "E:\\TEST";


    // ============================================================
    // FORM ITEM LENGTH
    // ============================================================

    private static final int REQUIRED_LENGTH =
        30;


    // ============================================================
    // RECORD GROUP COLUMN LENGTH
    // ============================================================

    private static final int RECORD_GROUP_NEW_LENGTH =
        30;


    // ============================================================
    // VARIABLE LENGTH
    // ============================================================

    private static final int LOCAL_VARIABLE_OLD_LENGTH =
        5;


    private static final int LOCAL_VARIABLE_NEW_LENGTH =
        30;


    // ============================================================
    // MAIN
    // ============================================================

    public static void main(String[] args) {


        System.out.println(
            "============================================================"
        );

        System.out.println(
            "              ORACLE FORMS JDAPI MODIFIER"
        );

        System.out.println(
            "============================================================"
        );

        System.out.println(
            "FMB Folder              : "
            + FMB_FOLDER
        );

        System.out.println(
            "Target Item Length      : "
            + REQUIRED_LENGTH
        );

        System.out.println(
            "Record Group Length     : "
            + RECORD_GROUP_NEW_LENGTH
        );

        System.out.println(
            "VARCHAR2 Length         : "
            + LOCAL_VARIABLE_OLD_LENGTH
            + " -> "
            + LOCAL_VARIABLE_NEW_LENGTH
        );

        System.out.println(
            "Program Units           : ENABLED"
        );

        System.out.println(
            "ALL Triggers            : ENABLED"
        );

        System.out.println(
            "Database Table          : NOT CHANGED"
        );

        System.out.println(
            "Database Column         : NOT CHANGED"
        );

        System.out.println(
            "ALTER TABLE             : NOT USED"
        );

        System.out.println(
            "============================================================"
        );

        System.out.println();


        // ========================================================
        // CHECK FOLDER
        // ========================================================

        File folder =
            new File(FMB_FOLDER);


        if (!folder.exists()) {

            System.out.println(
                "ERROR: Folder not found:"
            );

            System.out.println(
                FMB_FOLDER
            );

            return;
        }


        if (!folder.isDirectory()) {

            System.out.println(
                "ERROR: Path is not a directory:"
            );

            System.out.println(
                FMB_FOLDER
            );

            return;
        }


        // ========================================================
        // GET FMB FILES
        // ========================================================

        File[] fmbFiles =
            folder.listFiles(
                new FilenameFilter() {

                    public boolean accept(
                        File dir,
                        String name) {

                        return name
                            .toLowerCase(Locale.ROOT)
                            .endsWith(".fmb");
                    }
                }
            );


        if (
            fmbFiles == null
            ||
            fmbFiles.length == 0
        ) {

            System.out.println(
                "No FMB files found."
            );

            return;
        }


        // ========================================================
        // COUNTERS
        // ========================================================

        int totalFiles =
            0;

        int modifiedFiles =
            0;

        int unchangedFiles =
            0;

        int errorFiles =
            0;

        int totalItemsChanged =
            0;

        int totalRecordGroupChanged =
            0;

        int totalProgramUnitVariablesChanged =
            0;

        int totalTriggerVariablesChanged =
            0;

        int totalTriggersChanged =
            0;


        // ========================================================
        // PROCESS EACH FMB
        // ========================================================

        for (
            File file :
            fmbFiles
        ) {


            totalFiles++;


            System.out.println();

            System.out.println(
                "------------------------------------------------------------"
            );

            System.out.println(
                "FILE "
                + totalFiles
                + " OF "
                + fmbFiles.length
            );

            System.out.println(
                "Opening:"
            );

            System.out.println(
                file.getAbsolutePath()
            );

            System.out.println(
                "------------------------------------------------------------"
            );


            boolean modified =
                false;


            int itemCount =
                0;

            int recordGroupCount =
                0;

            int programUnitVariableCount =
                0;

            int triggerVariableCount =
                0;

            int triggerCount =
                0;


            FormModule form =
                null;


            try {


                String fmbFile =
                    file.getAbsolutePath();


                // =================================================
                // OPEN FMB
                // =================================================

                form =
                    FormModule.open(
                        fmbFile
                    );


                System.out.println(
                    "FMB Open Successful!"
                );


                System.out.println(
                    "Form Name: "
                    + form.getName()
                );


                System.out.println();


                // =================================================
                // 1. FORM ITEMS
                // =================================================

                System.out.println(
                    "============================================================"
                );

                System.out.println(
                    "1. CHECKING TARGET FORM ITEMS"
                );

                System.out.println(
                    "============================================================"
                );


                JdapiIterator blocks =
                    form.getBlocks();


                while (
                    blocks.hasNext()
                ) {


                    Block block =
                        (Block) blocks.next();


                    if (
                        block == null
                    ) {

                        continue;
                    }


                    String blockName =
                        block.getName();


                    JdapiIterator items =
                        block.getItems();


                    while (
                        items.hasNext()
                    ) {


                        Item item =
                            (Item) items.next();


                        if (
                            item == null
                        ) {

                            continue;
                        }


                        String itemName =
                            item.getName();


                        if (
                            itemName == null
                        ) {

                            continue;
                        }


                        if (
                            !isTargetItem(
                                itemName
                            )
                        ) {

                            continue;
                        }


                        int oldLength;


                        try {

                            oldLength =
                                item.getMaximumLength();

                        } catch (
                            Exception e
                        ) {

                            System.out.println(
                                "Cannot read Maximum Length: "
                                + itemName
                            );

                            continue;
                        }


                        System.out.println(
                            "Block       : "
                            + blockName
                        );

                        System.out.println(
                            "Item        : "
                            + itemName
                        );

                        System.out.println(
                            "Old Length  : "
                            + oldLength
                        );


                        if (
                            oldLength
                            <
                            REQUIRED_LENGTH
                        ) {


                            item.setMaximumLength(
                                REQUIRED_LENGTH
                            );


                            modified =
                                true;


                            itemCount++;


                            totalItemsChanged++;


                            System.out.println(
                                "UPDATED     : "
                                + oldLength
                                + " -> "
                                + REQUIRED_LENGTH
                            );


                        } else {


                            System.out.println(
                                "NO CHANGE"
                            );
                        }


                        System.out.println();
                    }
                }


                // =================================================
                // 2. RECORD GROUP COLUMNS
                // =================================================

                System.out.println(
                    "============================================================"
                );

                System.out.println(
                    "2. CHECKING RECORD GROUP COLUMNS"
                );

                System.out.println(
                    "============================================================"
                );


                JdapiIterator recordGroups =
                    form.getRecordGroups();


                while (
                    recordGroups.hasNext()
                ) {


                    RecordGroup recordGroup =
                        (RecordGroup)
                        recordGroups.next();


                    if (
                        recordGroup == null
                    ) {

                        continue;
                    }


                    String recordGroupName =
                        recordGroup.getName();


                    System.out.println(
                        "Record Group: "
                        + recordGroupName
                    );


                    JdapiIterator columns =
                        recordGroup
                        .getRecordGroupColumns();


                    while (
                        columns.hasNext()
                    ) {


                        RecordGroupColumn column =
                            (RecordGroupColumn)
                            columns.next();


                        if (
                            column == null
                        ) {

                            continue;
                        }


                        String columnName =
                            column.getName();


                        if (
                            columnName == null
                        ) {

                            continue;
                        }


                        int oldLength =
                            column.getMaximumLength();


                        System.out.println(
                            "  Column     : "
                            + columnName
                        );

                        System.out.println(
                            "  Old Length : "
                            + oldLength
                        );


                        if (
                            oldLength
                            <
                            RECORD_GROUP_NEW_LENGTH
                        ) {


                            column.setMaximumLength(
                                RECORD_GROUP_NEW_LENGTH
                            );


                            modified =
                                true;


                            recordGroupCount++;


                            totalRecordGroupChanged++;


                            System.out.println(
                                "  UPDATED    : "
                                + oldLength
                                + " -> "
                                + RECORD_GROUP_NEW_LENGTH
                            );


                        } else {


                            System.out.println(
                                "  NO CHANGE"
                            );
                        }


                        System.out.println();
                    }
                }


                // =================================================
                // 3. PROGRAM UNITS
                // =================================================

                System.out.println(
                    "============================================================"
                );

                System.out.println(
                    "3. CHECKING PROGRAM UNITS"
                );

                System.out.println(
                    "============================================================"
                );


                JdapiIterator programUnits =
                    form.getProgramUnits();


                while (
                    programUnits.hasNext()
                ) {


                    ProgramUnit programUnit =
                        (ProgramUnit)
                        programUnits.next();


                    if (
                        programUnit == null
                    ) {

                        continue;
                    }


                    String programUnitName =
                        programUnit.getName();


                    System.out.println(
                        "Program Unit: "
                        + programUnitName
                    );


                    String source;


                    try {

                        source =
                            programUnit
                            .getProgramUnitText();

                    } catch (
                        Exception e
                    ) {

                        System.out.println(
                            "Cannot read Program Unit."
                        );

                        continue;
                    }


                    if (
                        source == null
                        ||
                        source.length() == 0
                    ) {

                        System.out.println(
                            "No source."
                        );

                        System.out.println();

                        continue;
                    }


                    ChangeResult result =
                        updateVariables(
                            source
                        );


                    if (
                        result.changed
                    ) {


                        programUnit
                            .setProgramUnitText(
                                result.source
                            );


                        modified =
                            true;


                        programUnitVariableCount +=
                            result.count;


                        totalProgramUnitVariablesChanged +=
                            result.count;


                    }


                    System.out.println(
                        "Variables Changed: "
                        + result.count
                    );


                    System.out.println();
                }


                // =================================================
                // 4. ALL TRIGGERS
                // =================================================

                System.out.println(
                    "============================================================"
                );

                System.out.println(
                    "4. CHECKING ALL FORM TRIGGERS"
                );

                System.out.println(
                    "============================================================"
                );


                TriggerResult triggerResult =
                    processAllTriggers(
                        form
                    );


                if (
                    triggerResult.changed
                ) {


                    modified =
                        true;
                }


                triggerVariableCount =
                    triggerResult.variableCount;


                triggerCount =
                    triggerResult.triggerCount;


                totalTriggerVariablesChanged +=
                    triggerResult.variableCount;


                totalTriggersChanged +=
                    triggerResult.triggerCount;


                System.out.println();


                // =================================================
                // SAVE
                // =================================================

                if (
                    modified
                ) {


                    System.out.println(
                        "============================================================"
                    );

                    System.out.println(
                        "Saving FMB..."
                    );


                    form.save(
                        fmbFile
                    );


                    modifiedFiles++;


                    System.out.println(
                        "SAVED SUCCESSFULLY"
                    );


                    System.out.println(
                        "File                         : "
                        + fmbFile
                    );


                    System.out.println(
                        "Items Changed                : "
                        + itemCount
                    );


                    System.out.println(
                        "Record Group Changed         : "
                        + recordGroupCount
                    );


                    System.out.println(
                        "Program Unit Variables       : "
                        + programUnitVariableCount
                    );


                    System.out.println(
                        "Trigger Variables            : "
                        + triggerVariableCount
                    );


                    System.out.println(
                        "Triggers Changed             : "
                        + triggerCount
                    );


                    System.out.println(
                        "Database Table               : NOT CHANGED"
                    );


                    System.out.println(
                        "Database Column              : NOT CHANGED"
                    );


                    System.out.println(
                        "============================================================"
                    );


                } else {


                    unchangedFiles++;


                    System.out.println(
                        "No changes required."
                    );
                }


            } catch (
                Exception e
            ) {


                errorFiles++;


                System.out.println();

                System.out.println(
                    "ERROR PROCESSING FILE:"
                );

                System.out.println(
                    file.getName()
                );

                System.out.println(
                    "Reason:"
                );

                System.out.println(
                    e.getMessage()
                );


                e.printStackTrace();


            } finally {


                if (
                    form != null
                ) {


                    try {

                        form.destroy();

                        System.out.println(
                            "FMB released successfully."
                        );

                    } catch (
                        Exception e
                    ) {

                        System.out.println(
                            "Could not release FMB:"
                        );

                        System.out.println(
                            e.getMessage()
                        );
                    }
                }
            }
        }


        // ========================================================
        // FINAL SUMMARY
        // ========================================================

        System.out.println();

        System.out.println(
            "============================================================"
        );

        System.out.println(
            "                    FINAL SUMMARY"
        );

        System.out.println(
            "============================================================"
        );


        System.out.println(
            "Total FMB Files              : "
            + totalFiles
        );


        System.out.println(
            "Modified FMB Files           : "
            + modifiedFiles
        );


        System.out.println(
            "Unchanged FMB Files          : "
            + unchangedFiles
        );


        System.out.println(
            "Error FMB Files              : "
            + errorFiles
        );


        System.out.println();


        System.out.println(
            "Items Changed                : "
            + totalItemsChanged
        );


        System.out.println(
            "Record Group Changed         : "
            + totalRecordGroupChanged
        );


        System.out.println(
            "Program Unit Variables       : "
            + totalProgramUnitVariablesChanged
        );


        System.out.println(
            "Trigger Variables            : "
            + totalTriggerVariablesChanged
        );


        System.out.println(
            "Triggers Changed             : "
            + totalTriggersChanged
        );


        System.out.println();


        System.out.println(
            "DATABASE TABLE               : NOT CHANGED"
        );


        System.out.println(
            "DATABASE COLUMN              : NOT CHANGED"
        );


        System.out.println(
            "ALTER TABLE                  : NOT USED"
        );


        System.out.println(
            "============================================================"
        );

        System.out.println(
            "PROCESS COMPLETED"
        );

        System.out.println(
            "============================================================"
        );
    }


    // ============================================================
    // PROCESS ALL TRIGGERS
    // ============================================================

    private static TriggerResult processAllTriggers(
        JdapiObject root)
        throws Exception {


        TriggerResult result =
            new TriggerResult();


        scanChildrenForTriggers(
            root,
            result
        );


        return result;
    }


    // ============================================================
    // RECURSIVELY SCAN ALL JDAPI CHILD OBJECTS
    // ============================================================

    private static void scanChildrenForTriggers(
        JdapiObject parent,
        TriggerResult result)
        throws Exception {


        JdapiMetaObject meta =
            JdapiMetadata.getJdapiMetaObject(
                parent.getClass()
            );


        if (
            meta == null
        ) {

            return;
        }


        JdapiIterator props =
            meta.getChildObjectMetaProperties();


        while (
            props.hasNext()
        ) {


            JdapiMetaProperty prop =
                (JdapiMetaProperty)
                props.next();


            if (
                prop == null
            ) {

                continue;
            }


            if (
                !prop.allowGet()
            ) {

                continue;
            }


            JdapiIterator children;


            try {


                children =
                    parent.getChildObjectProperty(
                        prop.getPropertyId()
                    );


            } catch (
                Exception e
            ) {


                // Some JDAPI properties may not
                // be readable in every object.
                //
                // Skip them safely.

                continue;
            }


            if (
                children == null
            ) {

                continue;
            }


            while (
                children.hasNext()
            ) {


                JdapiObject child =
                    (JdapiObject)
                    children.next();


                if (
                    child == null
                ) {

                    continue;
                }


                // =================================================
                // CHECK IF THIS OBJECT IS A TRIGGER
                // =================================================

                if (
                    "Trigger".equals(
                        child.getClassName()
                    )
                ) {


                    processOneTrigger(
                        (Trigger) child,
                        result
                    );
                }


                // =================================================
                // CONTINUE RECURSIVELY
                // =================================================

                scanChildrenForTriggers(
                    child,
                    result
                );
            }
        }
    }


    // ============================================================
    // PROCESS ONE TRIGGER
    // ============================================================

    private static void processOneTrigger(
        Trigger trigger,
        TriggerResult result) {


        if (
            trigger == null
        ) {

            return;
        }


        String triggerName =
            trigger.getName();


        String source;


        try {


            source =
                trigger.getTriggerText();


        } catch (
            Exception e
        ) {


            System.out.println(
                "Cannot read Trigger: "
                + triggerName
            );


            return;
        }


        if (
            source == null
            ||
            source.length() == 0
        ) {

            return;
        }


        ChangeResult change =
            updateVariables(
                source
            );


        if (
            !change.changed
        ) {

            return;
        }


        try {


            trigger.setTriggerText(
                change.source
            );


            result.changed =
                true;


            result.variableCount +=
                change.count;


            result.triggerCount++;


            System.out.println(
                "TRIGGER UPDATED: "
                + triggerName
            );


            System.out.println(
                "  Variables Changed: "
                + change.count
            );


        } catch (
            Exception e
        ) {


            System.out.println(
                "Cannot update Trigger: "
                + triggerName
            );


            System.out.println(
                "Reason: "
                + e.getMessage()
            );
        }
    }


    // ============================================================
    // UPDATE VARCHAR2(5)
    // ============================================================

    private static ChangeResult updateVariables(
        String source) {


        ChangeResult result =
            new ChangeResult();


        if (
            source == null
            ||
            source.length() == 0
        ) {

            result.source =
                source;

            return result;
        }


        List<Token> tokens =
            tokenize(
                source
            );


        StringBuilder out =
            new StringBuilder();


        int copied =
            0;


        for (
            int i = 0;
            i + 4 < tokens.size();
            i++
        ) {


            Token name =
                tokens.get(i);


            String key =
                name.text.toUpperCase(
                    Locale.ROOT
                );


            // ====================================================
            // VALID IDENTIFIER
            // ====================================================

            if (
                !key.matches(
                    "[A-Z][A-Z0-9_$#]*"
                )
            ) {

                continue;
            }


            // ====================================================
            // DO NOT TOUCH:
            //
            // :BLOCK.ITEM
            // :GLOBAL.X
            // TABLE.COLUMN
            // BLOCK.ITEM
            // ====================================================

            if (
                i > 0
            ) {


                String previous =
                    tokens.get(i - 1).text;


                if (
                    previous.equals(":")
                    ||
                    previous.equals(".")
                ) {

                    continue;
                }
            }


            // ====================================================
            // TYPE POSITION
            // ====================================================

            int type =
                i + 1;


            // ====================================================
            // CONSTANT
            //
            // v_code CONSTANT VARCHAR2(5)
            // ====================================================

            if (
                type < tokens.size()
                &&
                tokens.get(type)
                    .text
                    .equalsIgnoreCase(
                        "CONSTANT"
                    )
            ) {

                type++;
            }


            if (
                type + 3 >= tokens.size()
            ) {

                continue;
            }


            // ====================================================
            // VARCHAR2
            // ====================================================

            if (
                !tokens.get(type)
                    .text
                    .equalsIgnoreCase(
                        "VARCHAR2"
                    )
            ) {

                continue;
            }


            // ====================================================
            // (
            // ====================================================

            if (
                !tokens.get(type + 1)
                    .text
                    .equals("(")
            ) {

                continue;
            }


            // ====================================================
            // LENGTH
            // ====================================================

            Token length =
                tokens.get(type + 2);


            if (
                !length.text.matches(
                    "[0-9]+"
                )
            ) {

                continue;
            }


            // ====================================================
            // )
            // ====================================================

            int close =
                type + 3;


            // ====================================================
            // CHAR / BYTE
            // ====================================================

            if (
                close < tokens.size()
                &&
                (
                    tokens.get(close)
                        .text
                        .equalsIgnoreCase(
                            "CHAR"
                        )
                    ||
                    tokens.get(close)
                        .text
                        .equalsIgnoreCase(
                            "BYTE"
                        )
                )
            ) {

                close++;
            }


            if (
                close >= tokens.size()
            ) {

                continue;
            }


            if (
                !tokens.get(close)
                    .text
                    .equals(")")
            ) {

                continue;
            }


            // ====================================================
            // ONLY VARCHAR2(5)
            // ====================================================

            int oldLength;


            try {


                oldLength =
                    Integer.parseInt(
                        length.text
                    );


            } catch (
                NumberFormatException e
            ) {

                continue;
            }


            if (
                oldLength
                !=
                LOCAL_VARIABLE_OLD_LENGTH
            ) {

                continue;
            }


            // ====================================================
            // REPLACE ONLY THE NUMBER
            // ====================================================

            out.append(
                source,
                copied,
                length.start
            );


            out.append(
                LOCAL_VARIABLE_NEW_LENGTH
            );


            copied =
                length.end;


            result.count++;


            result.changed =
                true;


            System.out.println(
                "    "
                + name.text
                + " VARCHAR2("
                + oldLength
                + ") -> VARCHAR2("
                + LOCAL_VARIABLE_NEW_LENGTH
                + ")"
            );
        }


        // ========================================================
        // REMAINING SOURCE
        // ========================================================

        out.append(
            source,
            copied,
            source.length()
        );


        result.source =
            out.toString();


        return result;
    }


    // ============================================================
    // TOKENIZER
    // ============================================================

    private static List<Token> tokenize(
        String source) {


        List<Token> list =
            new ArrayList<Token>();


        int i =
            0;


        while (
            i < source.length()
        ) {


            int start =
                i;


            char c =
                source.charAt(i);


            // ====================================================
            // WHITESPACE
            // ====================================================

            if (
                Character.isWhitespace(c)
            ) {

                i++;

                continue;
            }


            // ====================================================
            // -- COMMENT
            // ====================================================

            if (
                c == '-'
                &&
                i + 1 < source.length()
                &&
                source.charAt(i + 1) == '-'
            ) {


                i += 2;


                while (
                    i < source.length()
                    &&
                    source.charAt(i) != '\n'
                    &&
                    source.charAt(i) != '\r'
                ) {

                    i++;
                }


                continue;
            }


            // ====================================================
            // /* COMMENT */
            // ====================================================

            if (
                c == '/'
                &&
                i + 1 < source.length()
                &&
                source.charAt(i + 1) == '*'
            ) {


                int end =
                    source.indexOf(
                        "*/",
                        i + 2
                    );


                if (
                    end < 0
                ) {

                    i =
                        source.length();

                } else {

                    i =
                        end + 2;
                }


                continue;
            }


            // ====================================================
            // QUOTED STRING
            // ====================================================

            if (
                c == '\''
                ||
                c == '"'
            ) {


                i++;


                while (
                    i < source.length()
                ) {


                    char q =
                        source.charAt(i++);


                    if (
                        q == c
                    ) {


                        if (
                            i < source.length()
                            &&
                            source.charAt(i) == c
                        ) {

                            i++;

                        } else {

                            break;
                        }
                    }
                }


                list.add(
                    new Token(
                        "<quoted>",
                        start,
                        i
                    )
                );


                continue;
            }


            // ====================================================
            // IDENTIFIER / NUMBER
            // ====================================================

            if (
                Character.isLetterOrDigit(c)
                ||
                c == '_'
                ||
                c == '$'
                ||
                c == '#'
            ) {


                i++;


                while (
                    i < source.length()
                    &&
                    (
                        Character.isLetterOrDigit(
                            source.charAt(i)
                        )
                        ||
                        source.charAt(i) == '_'
                        ||
                        source.charAt(i) == '$'
                        ||
                        source.charAt(i) == '#'
                    )
                ) {

                    i++;
                }


                list.add(
                    new Token(
                        source.substring(
                            start,
                            i
                        ),
                        start,
                        i
                    )
                );


                continue;
            }


            // ====================================================
            // SYMBOL
            // ====================================================

            i++;


            list.add(
                new Token(
                    source.substring(
                        start,
                        i
                    ),
                    start,
                    i
                )
            );
        }


        return list;
    }


    // ============================================================
    // CHANGE RESULT
    // ============================================================

    private static class ChangeResult {


        String source;

        boolean changed =
            false;

        int count =
            0;
    }


    // ============================================================
    // TRIGGER RESULT
    // ============================================================

    private static class TriggerResult {


        boolean changed =
            false;

        int variableCount =
            0;

        int triggerCount =
            0;
    }


    // ============================================================
    // TOKEN
    // ============================================================

    private static class Token {


        String text;

        int start;

        int end;


        Token(
            String text,
            int start,
            int end
        ) {


            this.text =
                text;


            this.start =
                start;


            this.end =
                end;
        }
    }


    // ============================================================
    // TARGET FORM ITEMS
    // ============================================================

    private static boolean isTargetItem(
        String itemName) {


        return itemName.equalsIgnoreCase(
            "FF_CODE"
        )

        || itemName.equalsIgnoreCase(
            "MPO_CODE"
        )

        || itemName.equalsIgnoreCase(
            "FM_CODE"
        )

        || itemName.equalsIgnoreCase(
            "RM_CODE"
        )

        || itemName.equalsIgnoreCase(
            "AM_CODE"
        )

        || itemName.equalsIgnoreCase(
            "SM_CODE"
        )

        || itemName.equalsIgnoreCase(
            "P_CODE"
        )

        || itemName.equalsIgnoreCase(
            "BRAND_CODE"
        )

        || itemName.equalsIgnoreCase(
            "P_N_CODE"
        )

        || itemName.equalsIgnoreCase(
            "ITEM_CODE"
        )

        || itemName.equalsIgnoreCase(
            "PPM_CODE"
        )

        || itemName.equalsIgnoreCase(
            "RAW_CODE"
        )

        || itemName.equalsIgnoreCase(
            "INCREASE"
        );
    }
}