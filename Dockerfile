FROM amazonlinux:2023

ARG version=23.0.1.8-1
ARG package_version=1

RUN set -eux \
    && rpm --import file:///etc/pki/rpm-gpg/RPM-GPG-KEY-amazon-linux-2023 \
    && echo "localpkg_gpgcheck=1" >> /etc/dnf/dnf.conf \
    && CORRETO_TEMP=$(mktemp -d) \
    && pushd ${CORRETO_TEMP} \
    && RPM_LIST=("java-23-amazon-corretto-headless-$version.amzn2023.${package_version}.$(uname -m).rpm") \
    && for rpm in ${RPM_LIST[@]}; do \
    curl --fail -O https://corretto.aws/downloads/resources/$(echo $version | tr '-' '.')/${rpm} \
    && rpm -K "${CORRETO_TEMP}/${rpm}" | grep -F "${CORRETO_TEMP}/${rpm}: digests signatures OK" || exit 1; \
    done \
    && dnf install -y ${CORRETO_TEMP}/*.rpm \
    && popd \
    && rm -rf /usr/lib/jvm/java-23-amazon-corretto.$(uname -m)/lib/src.zip \
    && rm -rf ${CORRETO_TEMP} \
    && dnf clean all \
    && sed -i '/localpkg_gpgcheck=1/d' /etc/dnf/dnf.conf

ENV LANG=C.UTF-8
ENV JAVA_HOME=/usr/lib/jvm/java-23-amazon-corretto

ENV FAT_JAR=vertex-starter-1.0.0-SNAPSHOT-fat.jar
ENV APP_HOME=/usr/app

RUN mkdir -p /usr/app

EXPOSE 8888
COPY build/libs/$FAT_JAR $APP_HOME/

WORKDIR $APP_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec java -jar $FAT_JAR"]
