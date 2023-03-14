VERSION 1.0 CLASS
BEGIN
  MultiUse = -1  'True
END
Attribute VB_Name = "cApiSquash"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = False
Attribute VB_Exposed = True
'@Folder("API Squash")

Option Explicit

Const API_SQUASH_URL As String = "https://saas-ans02.henix.com/squash/api/rest/latest"
Const API_SQUASH_USER As String = "ans_api"
Const API_SQUASH_PASSWORD As String = "api2022"
Const ERROR_CODE As Integer = 10001
Const ERROR_SOURCE As String = "API Squash"
Const STR_BOUNDARY  As String = "-----VBAMacroBoundaryCYV93Ss57BpRp"

Private objHTTP As Object
Private lastResult As Object
Private lastResponse As String
Private lastQueryUrl As String
Private bDebug As Boolean
Public sLastError As String

' modif

Private Sub Class_Initialize()
    Set objHTTP = CreateObject("WinHttp.WinHttpRequest.5.1")
    bDebug = False
    lastQueryUrl = vbNullString
    lastResponse = vbNullString
End Sub

Private Sub errHandler(sMessage As String)
    sLastError = sMessage
    On Error Resume Next
    Err.Raise ERROR_CODE, ERROR_SOURCE, sMessage
    global_error_handling
    'defaultErrorDispay (sMessage)

End Sub

Public Sub debugLastQuery()
    Debug.Print "===================================================="
    Debug.Print lastQueryUrl
    Debug.Print "===================================================="
    Debug.Print lastResponse
End Sub

Public Sub setDebugMode(ByVal bState As Boolean)
    bDebug = bState
End Sub

Public Function getLastResult() As Object
    Set getLastResult = lastResult
End Function

Public Sub getList(ByVal sEntity As String, ByRef oResult As Collection)
    ' Dim oResult As Object
    apiCall "GET", "/" & sEntity & "?page=0&size=100"
    If lastResult.Count > 0 Then
        Set oResult = lastResult("_embedded")(vbNullString & sEntity & vbNullString)
    End If
End Sub

Public Sub modifyDetail(ByVal sEntity As String, ByVal iId As Long, Optional sComplement As String = vbNullString, Optional ByRef oParams As Variant = Nothing)
    Dim sBody As String
    
    If Not oParams Is Nothing Then
        sBody = ConvertToJson(oParams, " ")
    End If
   ' Debug.Print sBody
    apiCall "PATCH", "/" & sEntity & "/" & iId & IIf(sComplement <> vbNullString, "/" & sComplement, vbNullString), sBody
    ' If Not lastResult Is Nothing Then
    '     Set oResult = lastResult
    ' End If
End Sub

Public Sub getDetail(ByVal sEntity As String, ByVal sId As String, ByRef oResult As Object, Optional ByVal sSuffix As String = vbNullString, Optional sQueryString As String = vbNullString)
    
    apiCall "GET", "/" & sEntity & "/" & sId & IIf(sSuffix <> vbNullString, "/" & sSuffix, vbNullString) & IIf(sQueryString <> vbNullString, "?" & sQueryString, vbNullString)
    If Not lastResult Is Nothing Then
        Set oResult = lastResult
    End If
End Sub

Public Function createEntity(ByVal sParentEntity As String, ByVal sParentId As Long, ByVal sEntity As String, Optional ByRef oParams As Variant = Nothing) As Long
    Dim decodParam As Variant
    Dim decodValue As Variant
    Dim sBody As String
    
    ''  sParams = sParams & "&_type=" & sEntity
    '  decodParam = Split(sParams, "&")
    '  For i = 0 To UBound(decodParam)
    '      decodValue = Split(decodParam(i), "=")
    ''      decodValue(1) = URLEncode(decodValue(1) & "")
    '     decodParam(i) = Join(decodValue, "=")
    '  Next
    '  sParams = Join(decodParam, "&")
    If Not oParams Is Nothing Then
        sBody = ConvertToJson(oParams, " ")
    End If
    apiCall "POST", "/" & sParentEntity & "/" & sParentId & "/" & sEntity, sBody
    If Not lastResult Is Nothing Then
        createEntity = lastResult("id")
    End If
End Function

Private Sub apiCall(ByVal sMethod As String, ByVal sPath As String, Optional ByVal sBody As String = vbNullString, Optional sContentType As String = "application/json", Optional sQueryString As String = "size=100")
    
    sLastError = ""
    lastQueryUrl = API_SQUASH_URL & sPath & IIf(sQueryString <> "", "?" & sQueryString, "")
    
    With objHTTP
        .Open sMethod, lastQueryUrl, False
        .SetRequestHeader "User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)"
        .SetRequestHeader "Content-Type", sContentType
        If sBody <> "" Then
          .SetRequestHeader "Content-Length", Len(sBody)
        End If
        
        .SetRequestHeader "Authorization", "Basic " & Base64Encode(API_SQUASH_USER & ":" & API_SQUASH_PASSWORD)

    End With
 '    On Error Resume Next
 ' Debug.Print sBody
    On Error GoTo errHandler
    objHTTP.Send (sBody)
    On Error Resume Next
    lastResponse = objHTTP.responseText ' Utf2Uni(objHTTP.responseText)
   Set lastResult = ParseJson(lastResponse)
   
    If bDebug Then
       Debug.Print sMethod & " : " & lastQueryUrl
        Debug.Print "Content-Type : " & sContentType
        If sBody <> "" Then
            Debug.Print "Content-Length : " & Len(sBody)
            If Len(sBody) > 1000 Then
                Debug.Print "Body : " & vbCr & Left$(sBody, 800) & vbCr & "[...]" & vbCr & Right$(sBody, 800) & vbCr
            Else
                 Debug.Print "Body : " & sBody & vbCr
            End If
        End If
        Debug.Print "Response :" & lastResponse
    End If
    'lastResponse = Replace(lastResponse, "’", "#")
    If InStr(lastResponse, "Bad Request") <> 0 Then
        errHandler ("==== API error =================================== " & vbCr _
                  & vbCr & "query : " & sMethod & " " & sPath _
                  & vbCr & "Bad resquest" _
                  & vbCr & vbCr & "Response : " & vbCr & lastResponse _
                    )
        Set lastResult = New Collection
    ElseIf lastResult.Exists("exception") Then
        If lastResult("exception") = "org.squashtest.tm.exception.requirement.IllegalRequirementModificationException" Then   ' modif impossible cause status
            sLastError = "IllegalRequirementModificationException"
        Else
        
            Debug.Print "==== API error =================================== " & sPath
            Debug.Print sBody
            Debug.Print "==================================================="
            errHandler ("==== API error =================================== " & vbCr _
                      & API_SQUASH_URL & vbCr _
                      & vbCr & "query : " & vbCr & sMethod & " " & sPath _
                      & vbCr & vbCr & "body : " & vbCr & sBody _
                      & vbCr & vbCr & "Response : " & vbCr & lastResponse _
                        )
        End If
        Set lastResult = Nothing
    Else
   
    
     
        If lastResult.Exists("fieldValidationErrors") Then
            If lastResult("fieldValidationErrors")(1)("errorMessage") = "Can not change an approved requirement" _
                Or lastResult("fieldValidationErrors")(1)("errorMessage") = "Can not change an obsolete requirement" _
                Or lastResult("fieldValidationErrors")(1)("errorMessage") = "Only requirements which have the status 'Under review' can have their status updated to 'Approved'." Then
                 sLastError = "IllegalRequirementModificationException"
                 Set lastResult = Nothing
            Else
                sLastError = lastResult("fieldValidationErrors")(1)("errorMessage")
            End If
        End If
        
        'If lastResult("status") <> "" And lastResult("status") <> "UNDEFINED" Then
        '    errHandler ("==== API error =================================== " & vbCr _
        '    & vbCr & "query : " & sPath _
        '    & vbCr & "status : " & lastResult("status") & " / " & lastResult("error") _
        '    & vbCr & "body : " & sBody _
        '    )
        '    Set lastResult = Nothing
        'End If
    End If
    
    Exit Sub
    
errHandler:
    Set lastResult = New Collection
    errHandler "Problème de connexion avec le serveur Squash " & API_SQUASH_URL
End Sub

Public Sub uploadAttachement(sOwner As String, sOwnerID As Long, sFname As String)

    Dim sFileContent As String
    sFileContent = getFileBoundary(sFname)
    Application.SendKeys "^g ^a {DEL}"
    'apiCall "POST", "/" & sOwner & "/" & sOwnerID & "/attachments", sFileContent, "multipart/form-data; boundary=----" & STR_BOUNDARY, ""
    uploadFile "/" & sOwner & "/" & sOwnerID & "/attachments", sFname, "files", "foo=bar"
End Sub
Private Function getFileBoundary(sFileName As String) As String
    Dim nFile           As Integer
    Dim baBuffer()      As Byte
    Dim sPostData       As String
 
   ' sFileName = "C:\Users\jrenier\Documents\todo-mes.txt"
    
    '--- read file
    nFile = FreeFile
    Open sFileName For Binary Access Read As nFile
    If LOF(nFile) > 0 Then
        ReDim baBuffer(0 To LOF(nFile) - 1) As Byte
        Get nFile, , baBuffer
        sPostData = StrConv(baBuffer, vbUnicode)
    End If
    Close nFile
'sPostData = "toto"
    '--- prepare body
    sPostData = "------" & STR_BOUNDARY & vbCrLf & _
        "Content-Disposition: form-data; name=""files""; filename=""" & Mid$(sFileName, InStrRev(sFileName, "\") + 1) & """" & vbCrLf & _
        "Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" & vbCrLf & vbCrLf & _
        sPostData & vbCrLf & _
        "------" & STR_BOUNDARY & "--"
    
   getFileBoundary = sPostData
   
End Function
 
Private Function pvToByteArray(sText As String) As Byte()
    pvToByteArray = StrConv(sText, vbFromUnicode)
End Function
Private Function uploadFile(sQueryPath, strFilePath, strFileField, strDataPairs, Optional sContentType As String = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    'Uses POST to upload a file and miscellaneous form data
    'strFileField is the web page equivalent form field name for the file (File1)
    'strDataPairs are pipe-delimited form data pairs (foo=bar|snap=crackle)
    
    
    Dim ado, rs
    Dim lngCount
    Dim bytFormData, bytFormStart, bytFormEnd, bytFile
    Dim strFormStart, strFormEnd, strDataPair
    Dim web
    Dim strUploadUrl As String
    
    strUploadUrl = API_SQUASH_URL & sQueryPath


    Const adLongVarBinary = 205
    'Read the file into a byte array
    Set ado = CreateObject("ADODB.Stream")
    ado.Type = 1
    ado.Open
    ado.LoadFromFile strFilePath
    bytFile = ado.Read
    ado.Close
    'Create the multipart form data.
    'Define the end of form
    strFormEnd = vbCrLf & "--" & STR_BOUNDARY & "--" & vbCrLf
    'First add any ordinary form data pairs
    strFormStart = ""
    For Each strDataPair In Split(strDataPairs, "|")
        strFormStart = strFormStart & "--" & STR_BOUNDARY & vbCrLf
        strFormStart = strFormStart & "Content-Disposition: form-data; "
        strFormStart = strFormStart & "name=""" & Split(strDataPair, "=")(0) & """"
        strFormStart = strFormStart & vbCrLf & vbCrLf
        strFormStart = strFormStart & Split(strDataPair, "=")(1)
        strFormStart = strFormStart & vbCrLf
    Next
    'Now add the header for the uploaded file
    strFormStart = strFormStart & "--" & STR_BOUNDARY & vbCrLf
    strFormStart = strFormStart & "Content-Disposition: form-data; "
    strFormStart = strFormStart & "name=""" & strFileField & """; "
    strFormStart = strFormStart & "filename=""" & Mid(strFilePath, InStrRev(strFilePath, "\") + 1) & """"
    strFormStart = strFormStart & vbCrLf
    strFormStart = strFormStart & "Content-Type: " & sContentType
    strFormStart = strFormStart & vbCrLf & vbCrLf
    'Create a recordset large enough to hold everything
    Set rs = CreateObject("ADODB.Recordset")
    rs.Fields.Append "FormData", adLongVarBinary, Len(strFormStart) + LenB(bytFile) + Len(strFormEnd)
    rs.Open
    rs.AddNew
    'Convert form data so far to zero-terminated byte array
    For lngCount = 1 To Len(strFormStart)
        bytFormStart = bytFormStart & ChrB(asc(Mid(strFormStart, lngCount, 1)))
    Next
    rs("FormData").AppendChunk bytFormStart & ChrB(0)
    bytFormStart = rs("formData").GetChunk(Len(strFormStart))
    rs("FormData") = ""
    'Get the end boundary as a zero-terminated byte array
    For lngCount = 1 To Len(strFormEnd)
        bytFormEnd = bytFormEnd & ChrB(asc(Mid(strFormEnd, lngCount, 1)))
    Next
    rs("FormData").AppendChunk bytFormEnd & ChrB(0)
    bytFormEnd = rs("formData").GetChunk(Len(strFormEnd))
    rs("FormData") = ""
    'Now merge it all
    rs("FormData").AppendChunk bytFormStart
    rs("FormData").AppendChunk bytFile
    rs("FormData").AppendChunk bytFormEnd
    bytFormData = rs("FormData")
    rs.Close
    'Upload it
        
    Set web = CreateObject("WinHttp.WinHttpRequest.5.1")
    web.Open "POST", strUploadUrl, False
    web.SetRequestHeader "Content-Type", "multipart/form-data; boundary=" & STR_BOUNDARY
    web.SetRequestHeader "Authorization", "Basic " & Base64Encode(API_SQUASH_USER & ":" & API_SQUASH_PASSWORD)
    web.Send bytFormData
    
   lastResponse = Utf2Uni(web.responseText)
    If bDebug Then
        Debug.Print "===upload file to : " & strUploadUrl
        Debug.Print "status " & web.Status
        Debug.Print "response " & web.responseText
    End If
    
End Function

