NAME		:= Ft_vox
SRC_PATH	:= src/
CLASS_PATH	:= class/
BIN_PATH	:= bin/
LIB_PATH	:= lib/jar/
NATIVES_PATH=  lib/natives_linux_x64/
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
RUNFLAGS	= -Xms4G -Xmx4G --enable-native-access=ALL-UNNAMED

ifdef LINUX_ARM32
	NATIVES_PATH= lib/natives_linux_arm32/
endif
ifdef LINUX_ARM64
	NATIVES_PATH= lib/natives_linux_arm64/
endif
ifdef LINUX_MIPS64
	NATIVES_PATH= lib/natives_linux_mips64/
endif
ifdef LINUX_PPC64LE
	NATIVES_PATH= lib/natives_linux_ppc64le/
endif
ifdef LINUX_RISCV64
	NATIVES_PATH= lib/natives_linux_riscv64/
endif
ifdef LINUX_X64
endif
ifdef MAC_ARM64
	NATIVES_PATH= lib/natives_macos_arm64/
	RUNFLAGS	+= -XstartOnFirstThread
endif
ifdef MAC_X64
	NATIVES_PATH= lib/natives_macos_x64/
	RUNFLAGS	+= -XstartOnFirstThread
endif
ifdef WINDOWS_ARM64
	NATIVES_PATH= lib/natives_windows_arm64/
endif
ifdef WINDOWS_X64
	NATIVES_PATH= lib/natives_windows_x64/
endif
ifdef WINDOWS_X86
	NATIVES_PATH= lib/natives_windows_x86/
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

linux_arm32:
	@ $(MAKE) LINUX_ARM32=1
.PHONY: linux_arm32

linux_arm64:
	@ $(MAKE) LINUX_ARM64=1
.PHONY: linux_arm64

linux_mips64:
	@ $(MAKE) LINUX_MIPS64=1
.PHONY: linux_mips64

linux_ppc64le:
	@ $(MAKE) LINUX_PPC64LE=1
.PHONY: linux_ppc64le

linux_riscv64:
	@ $(MAKE) LINUX_RISCV64=1
.PHONY: linux_riscv64

linux_x64:
	@ $(MAKE) LINUX_X64=1
.PHONY: linux_x64

mac_arm64:
	@ $(MAKE) MAC_ARM64=1
.PHONY:	mac_arm64

mac_x64:
	@ $(MAKE) MAC_X64=1
.PHONY:	mac_x64

windows_arm64:
	@ $(MAKE) WINDOWS_ARM64=1
.PHONY:	windows_arm64

windows_x64:
	@ $(MAKE) WINDOWS_X64=1
.PHONY:	windows_x64

windows_x86:
	@ $(MAKE) WINDOWS_X86=1
.PHONY:	windows_x86
