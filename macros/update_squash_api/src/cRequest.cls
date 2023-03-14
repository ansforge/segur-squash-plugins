VERSION 1.0 CLASS
BEGIN
  MultiUse = -1  'True
END
Attribute VB_Name = "cRequest"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = False
Attribute VB_Exposed = False
Option Explicit


Public sAction As String
Public json As Variant

Public iReqId As Long
Public sReqRef As String

Public Sub Class_Initialize()
    ' Set json = New Dictionary
    Set json = ParseJson("{ '_type':'requirement', 'current_version':{ '_type' : 'requirement-version'} }")
    iReqId = 0
    sReqRef = ""
End Sub

Public Sub p()
    'Debug.Print jsonConverter.ConvertToJson(json("current_version"), Whitespace:=2)
    Debug.Print ConvertToJson(json, whiteSpace:=2)
End Sub

Public Sub setReqId(ByVal iId As Long)
    If IsNumeric(iId) Then
        iReqId = iId
    End If
End Sub

Public Sub setReqRef(ByVal ref As String)
    sReqRef = ref
End Sub

Public Sub setRequest(sCodeAction)
    If sCodeAction = "U" Then
        sAction = "UPDATE"
    ElseIf sCodeAction = "C" Then
        sAction = "CREATE"
    ElseIf sCodeAction = "" Then
        sAction = "ERR"
    Else
        sAction = sCodeAction
    End If
End Sub

Public Sub setCurrentVersionAttribute(key As String, value As Variant)
    If IsObject(value) Then
        Set json("current_version")(key) = value
    Else
        json("current_version")(key) = value
    End If
End Sub

Public Sub setCufValue(key As String, value As Variant)
    Dim cufObject As Dictionary
    
    If VarType(json("current_version")("custom_fields")) = 0 Then
        Set json("current_version")("custom_fields") = New Collection
    End If
    On Error GoTo 0
    'If Not KeyExistsInCollection("custom_fields", json("current_version")) Then
    '    Set json("current_version")("custom_fields") = New Collection
    'End If
    Set cufObject = New Dictionary
    cufObject.Add key:="code", Item:=UCase(key)
    cufObject.Add key:="value", Item:=value
    json("current_version")("custom_fields").Add key:=key, Item:=cufObject
End Sub

Public Sub runRequest()

    Dim oApi As cApiSquash
    Dim unlockQuery As Object
    

    If sAction <> "UPDATE" Then
        logError iReqId, "/  " & sReqRef & " : " & sAction
    Else
        If iReqId = 0 Then
            Err.Raise -1, "updateSquash", "req id manquant"
        End If
    
        Set oApi = New cApiSquash
   
        oApi.modifyDetail "requirements", iReqId, "", json
        If oApi.sLastError = "IllegalRequirementModificationException" Then
            ' pb status : il faut delocker
            Set unlockQuery = ParseJson("{ '_type':'requirement', 'current_version':{ '_type' : 'requirement-version', 'status':'UNDER_REVIEW'} }")
            oApi.modifyDetail "requirements", iReqId, "", unlockQuery
            If oApi.sLastError = "IllegalRequirementModificationException" Then
                logError iReqId, sReqRef & " : ERROR : modification impossible cause status peut être OBSOLETE" ' toujours pas ? l'exigence est obsolète ; on ne peut rien faire
            Else
                If json("current_version")("status") = "" Then
                    json("current_version")("status") = "APPROVED"
                    'json("current_version").Add key:="status", Item:="APPROVED"
                End If
                oApi.modifyDetail "requirements", iReqId, "", json
                logError iReqId, sReqRef & " : ok après modification du status"
            End If
        ElseIf oApi.sLastError <> "" Then
            Err.Raise -1, "updateSquash", oApi.sLastError
        Else
            logError iReqId, sReqRef & " : ok"
 
        End If

    End If

End Sub

