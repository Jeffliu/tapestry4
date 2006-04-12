# $Id$
#
# Tapestry Web Application Framework
# Copyright (c) 2000 by Howard Ship and Primix Solutions
#
# Primix Solutions
# One Arsenal Marketplace
# Watertown, MA 02472
# http://www.primix.com
# mailto:hship@primix.com
# 
# This library is free software.
# 
# You may redistribute it and/or modify it under the terms of the GNU
# Lesser General Public License as published by the Free Software Foundation.
#
# Version 2.1 of the license should be included with this distribution in
# the file LICENSE, as well as License.html. If the license is not
# included with this distribution, you may find a copy at the FSF web
# site at 'www.gnu.org' or 'www.fsf.org', or you may write to the
# Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139 USA.
#
# This library is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# Lesser General Public License for more details.

include $(SYS_MAKEFILE_DIR)/CommonRules.mk

clean: clean-root module-clean

clean-root:
	@$(ECHO) "\n*** Cleaning ... ***\n"
	@$(RMDIRS) $(SYS_BUILD_DIR_NAME) $(JAR_FILE)

compile: setup-catalogs
	@$(RECURSE) POST_SETUP=t inner-compile
	
copy-resources: setup-catalogs
	@$(RECURSE) POST_SETUP=t inner-copy-resources

install: setup-catalogs
	@$(RECURSE) POST_SETUP=t inner-install

# Rule to force a rebuild of just the catalogs

catalog: initialize
	@$(RM) --force $(MOD_JAVA_CATALOG) $(MOD_RMI_CLASS_CATALOG) $(MOD_RESOURCE_CATALOG)
	@$(RECURSE)  SETUP_CATALOGS=t inner-setup-catalogs
	
setup-catalogs: initialize
	@$(RECURSE) SETUP_CATALOGS=t inner-setup-catalogs
	
initialize: module-initialize

# The force rule forces a recompile of all Java classes

force: setup-catalogs
	@$(RM) $(MOD_JAVA_STAMP_FILE)
	@$(RECURSE) POST_SETUP=t inner-compile

# Used to allow the inner targets to do something, even if fully up-to date.

DUMMY_FILE := $(SYS_BUILD_DIR_NAME)/dummy
	
ifdef POST_SETUP

# Build the compile-time classpath

_ABSOLUTE_DIRS := $(call JBE_CANONICALIZE,$(MOD_BUILD_DIR) $(MOD_CLASS_DIR))

ABSOLUTE_MOD_BUILD_DIR := $(word 1,$(_ABSOLUTE_DIRS))
ABSOLUTE_CLASS_DIR := $(word 2,$(_ABSOLUTE_DIRS))

FINAL_CLASSPATH = $(call JBE_CANONICALIZE, -classpath \
	$(FINAL_SOURCE_DIR) $(MOD_CLASS_DIR) $(MOD_CLASSPATH) $(SITE_CLASSPATH) $(LOCAL_CLASSPATH))
	
FINAL_CLASSPATH_OPTION = -classpath "$(FINAL_CLASSPATH)"
	
FINAL_JAVAC_OPT = $(strip \
	-d $(ABSOLUTE_CLASS_DIR) \
	$(FINAL_CLASSPATH_OPTION) \
	$(MOD_JAVAC_OPT) \
	$(SITE_JAVAC_OPT) \
	$(LOCAL_JAVAC_OPT) \
	$(JAVAC_OPT))
	
FINAL_RMIC_OPT = $(strip \
	$(FINAL_JAVAC_OPT) \
	$(MOD_RMIC_OPT) \
	$(SITE_RMIC_OPT) \
	$(LOCAL_RMIC_OPT) \
	$(RMIC_OPT))
	
inner-compile: $(MOD_JAVA_STAMP_FILE) $(RMI_STAMP_FILE)
	@$(TOUCH) $(DUMMY_FILE)
	
inner-copy-resources: $(MOD_META_STAMP_FILE) $(RESOURCE_STAMP_FILE)
	@$(TOUCH) $(DUMMY_FILE)

# The java catalog has the names of all .java files, including
# a prefix that identifies the root of the source code tree;
# since we change to the source code tree root directory,
# we need to strip off that prefix.

_JAVA_FILES := $(shell $(CAT) $(MOD_JAVA_CATALOG))

$(MOD_JAVA_STAMP_FILE): $(_JAVA_FILES)
ifneq "$(_JAVA_FILES)" ""
	@$(ECHO) "\n*** Compiling ... ***\n"
	$(CD) $(FINAL_SOURCE_DIR) ; \
	$(JAVAC) $(FINAL_JAVAC_OPT) $(patsubst $(FINAL_SOURCE_DIR)$(SLASH)%, \
	  	%, $?)
else
	@$(ECHO) "\n*** Nothing to compile ***\n"
endif
	@$(TOUCH) $@

# Read the catalog file

_RMI_CLASS_NAMES := $(shell $(CAT) $(MOD_RMI_CLASS_CATALOG))

# Find the name of each RMI implementation class.  This is the
# name of the corresponding .class file, inside the $(MOD_CLASS_DIR).

_RMI_CLASS_FILES := \
	$(addprefix $(MOD_CLASS_DIR)$(SLASH), \
		$(addsuffix .class, \
			$(subst $(DOT),$(SLASH),$(_RMI_CLASS_NAMES))))

# Here's where it gets real tricky; we need to reverse the prior
# process and get BACK to the class name.

$(RMI_STAMP_FILE): $(_RMI_CLASS_FILES)
ifneq "$(_RMI_CLASS_NAMES)" ""
	@$(ECHO) "\n*** Compiling RMI stubs and skeletons ... ***\n"
	$(RMIC) $(FINAL_RMIC_OPT) \
		$(subst $(SLASH),$(DOT), \
			$(subst .class,$(EMPTY), \
				$(subst $(MOD_CLASS_DIR)$(SLASH),$(EMPTY), $?)))
endif
	@$(TOUCH) $@

# Whenever an operation changes something inside the Jar staging area, it touches
# a stamp.  Whenever one of those stamps change, we touch the master-stamp (dirty jar)
# to force the actual Jar to be rebuilt.
# Additional pre-jar behaviours can be added by creating more dependencies
# for $(MOD_DIRTY_JAR_STAMP_FILE)

# Note:  for some reason (is this a make bug), if there are multiple
# rules setting dependencies, then the command gets executed even though
# $? is empty.  This occurs with War.mk and WebLogic.mk that need to
# add additional dependencies to dirty jar stamp (to copy additional resources
# and such).  Go figure.

$(MOD_DIRTY_JAR_STAMP_FILE): $(RMI_STAMP_FILE) $(MOD_JAVA_STAMP_FILE) \
	$(RESOURCE_STAMP_FILE) $(MOD_META_STAMP_FILE)
	@$(if $?,$(TOUCH) $@)

# The catalog file has the path name, including the relative
# path to the source code root directory.  Like the Java files
# above, we change to the source code root directory and need
# to strip a prefix off of the name before it is useful.

_RESOURCE_FILES := $(shell $(CAT) $(MOD_RESOURCE_CATALOG))

$(RESOURCE_STAMP_FILE): $(_RESOURCE_FILES)
ifneq "$(_RESOURCE_FILES)" ""
	@$(ECHO) "\n*** Copying package resources ...***\n"
	@$(ECHO) Copying: $(notdir $?)
	@$(CD) $(FINAL_SOURCE_DIR) ; \
	$(CP) -f -P $(subst $(FINAL_SOURCE_DIR)$(SLASH),$(EMPTY),$?) \
		 $(ABSOLUTE_CLASS_DIR)
endif
	@$(TOUCH) $@

# End of the POST_SETUP block
	
endif

ifdef SETUP_CATALOGS

inner-setup-catalogs: $(MOD_JAVA_CATALOG) $(MOD_RMI_CLASS_CATALOG) $(MOD_RESOURCE_CATALOG)
	@$(TOUCH) $(DUMMY_FILE)
	
ABSOLUTE_MOD_BUILD_DIR := $(call JBE_CANONICALIZE, $(MOD_BUILD_DIR))

# Rules for rebuilding the catalog by visiting each
# Package.  Certain types of modules have no Java source (no PACKAGES are defined)
# but that's OK.

$(MOD_JAVA_CATALOG) $(MOD_RMI_CLASS_CATALOG) $(MOD_RESOURCE_CATALOG):
	@$(ECHO) > $(MOD_JAVA_CATALOG)
	@$(ECHO) > $(MOD_RMI_CLASS_CATALOG)
	@$(ECHO) > $(MOD_RESOURCE_CATALOG)
ifneq "$(PACKAGES)" ""
	@for package in $(PACKAGES) ; do \
	  $(RECURSE) PACKAGE_RECURSE=t PACKAGE="$$package" \
	  ABSOLUTE_MOD_BUILD_DIR="$(ABSOLUTE_MOD_BUILD_DIR)" catalog-package ; \
	done
endif

# End of SETUP_CATALOGS block

endif


ifdef PACKAGE_RECURSE

# A few rules used with recursion.  Recursion works by re-invoking
# make in the Module directory, by specifying a value for PACKAGE on
# the command line (in addition to a target).

# Convert each '.' to a path seperator

PACKAGE_DIR := $(subst $(PERIOD),$(SLASH),$(PACKAGE))

_FINAL_PACKAGE_DIR := $(FINAL_SOURCE_DIR)$(SLASH)$(PACKAGE_DIR)


catalog-package:
	@$(ECHO) "\n*** Cataloging package $(PACKAGE) ... ***\n"
	@if [ ! -r $(_FINAL_PACKAGE_DIR)/Makefile ] ; \
	then _mopt=--makefile=$(SYS_MAKEFILE_DIR)/DefaultPackage.mk ; \
	fi ; \
	$(MAKE) --directory=$(_FINAL_PACKAGE_DIR) $$_mopt \
		MOD_BUILD_DIR="$(ABSOLUTE_MOD_BUILD_DIR)" \
		MOD_PACKAGE_DIR="$(PACKAGE_DIR)" \
		MOD_SOURCE_DIR_PREFIX="$(FINAL_SOURCE_DIR)$(SLASH)"
	
# End of PACKAGE_RECURSE block

endif


javadoc:
ifeq "$(JAVADOC_DIR)" ""
	$(error Must define JAVADOC_DIR in Makefile)
endif
ifeq "$(PACKAGES)" ""
	$(error Must define PACKAGES in Makefile)
endif
	@$(ECHO) "\n*** Generating Javadoc ... ***\n"
	@$(MKDIRS) $(JAVADOC_DIR)
	$(JAVADOC) -d $(JAVADOC_DIR) -sourcepath $(FINAL_SOURCE_DIR) \
	-classpath "$(call JBE_CANONICALIZE,-classpath $(MOD_CLASSPATH) $(LOCAL_CLASSPATH) $(MOD_CLASS_DIR))" \
	$(JAVADOC_OPT) $(PACKAGES)

	
FINAL_META_RESOURCES := $(strip $(MOD_META_RESOURCES) $(META_RESOURCES))

$(MOD_META_STAMP_FILE): $(FINAL_META_RESOURCES)
ifneq "$(FINAL_META_RESOURCES)" ""
	@$(ECHO) "\n*** Copying META-INF resources ... ***\n"
	@$(ECHO) Copying: $(notdir $?)
	@$(CP) -f $? $(MOD_META_INF_DIR)
	@$(TOUCH) $(MOD_DIRTY_JAR_STAMP_FILE)
endif
	@$(TOUCH) $@ 

initialize: setup-jbe-util
	@$(MKDIRS) $(MOD_BUILD_DIR)

# May be implemented

.PHONY: module-clean

.PHONY: inner-compile inner-copy-resources
.PHONY: clean clean-root
.PHONY: setup-catalogs catalog-package
.PHONY: compile copy-resources javadoc
.PHONY: setup-jbe-util
.PHONY: inner-setup-catalogs
.PHONY: install
.PHONY: initialize module-initialize