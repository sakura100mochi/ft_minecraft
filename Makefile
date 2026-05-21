NAME		:= Ft_vox
SRC_PATH	:= src/
CLASS_PATH	:= class/
BIN_PATH	:= bin/
LIB_PATH	:= lib/jar/
NATIVES_PATH=  lib/natives-linux/
SRC_FILES	:= $(shell find $(SRC_PATH) -name "*.java")
CLASS_FILES	:= $(SRC_FILES:$(SRC_PATH)%.java=%.class)
RUN_FILES	:= Main
CLASSES		:= $(addprefix $(CLASS_PATH), $(CLASS_FILES))
JARS		:= $(wildcard $(LIB_PATH)*.jar)
JARS_CP		:= $(subst $(empty) $(empty),:,$(strip $(JARS)))
JAVA_RUN_CP	:= $(CLASS_PATH):$(JARS_CP)
JAVA		:= java
JAVAC		:= javac
JFLAGS		:= -cp $(JARS_CP)
RUNFLAGS	= -Xms4G -Xmx4G
ifdef MAC
	NATIVES_PATH= lib/natives-macos/
	RUNFLAGS	+= -XstartOnFirstThread
endif


##------------color-----------##
CHECK		= \033[36m[\033[0m✔\033[36m]\033[0m
REMOVE		= \033[38;5;211m[\033[0m✘\033[38;5;211m]\033[0m
GENERATE	= \033[38;5;227m[\033[0m➤\033[38;5;227m]\033[0m
BLUE		= \033[1;36m
YELLOW		= \033[38;5;227m
RED			= \033[38;5;211m
RESET		= \033[0m
##-----------------------------##

all:	$(NAME) run
.PHONY:	all

$(NAME):	$(CLASSES)
	@ echo "$(CHECK) $(BLUE)finish Compiling $(NAME). $(RESET)\n"

$(CLASS_PATH)%.class:$(SRC_PATH)%.java $(INCLUDES)
	@ mkdir -p ./$(CLASS_PATH)
	@ $(JAVAC) $(JFLAGS) $(SRC_FILES) -d $(CLASS_PATH)
	@ printf "$(GENERATE) $(YELLOW)Generating $@... %-50.50s\r$(RESET)\n"

run:
	@ $(JAVA) -cp $(JAVA_RUN_CP) -Djava.library.path=$(NATIVES_PATH) $(RUNFLAGS) $(RUN_FILES)
.PHONY:	run

download:
	@ bash LoadLibrary.sh
.PHONY: download

clean:
	@ $(RM) -r ./$(CLASS_PATH)
	@ printf "$(REMOVE) $(RED)$(NAME) : Remove class files.$(RESET)\n"
.PHONY:	clean

fclean:	clean
	@ $(RM) -r ./$(BIN_PATH)
	@ printf "$(REMOVE) $(RED)$(NAME) : Remove bin files.$(RESET)\n"
.PHONY:	fclean

re:	fclean all
.PHONY:	re

mac:
	@ $(MAKE) MAC=1
.PHONY:	mac