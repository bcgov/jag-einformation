## Templates to create/update nginx containers

### Command to execute template
1) Login to OC using login command
2) Run below command in each env. namespace dev/test/prod
   ``oc process -f nginx-jag-einformation.yaml --param-file=nginx-jag-einformation.env | oc apply -f -``
