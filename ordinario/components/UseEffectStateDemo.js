import React, {useState, useEffect} from "react";
import { StyleSheet, Text } from "react-native";

export default ()=>{
    const [carga, setCarga]=useState(true)
    const [usuarios, setUsuarios]=useState([])
/* NO SE DEBE DE HACER
    useEffect(
        async ()=>{
            const result= await fetch('https://jsonplaceholder.typicode.com/users')
            const json= await result.json()
            setUsuarios(json)
            setCarga(false)
        },[]
    )
*/
    const handleCarga = async () =>{
         const result= await fetch('https://jsonplaceholder.typicode.com/users')
            const json= await result.json()
            setUsuarios(json)
            setCarga(false)
    }

    useEffect(()=>{
        handleCarga()
    },[])
    
    return(
        <Text>{carga?"Cargando datos...":usuarios[0].name}</Text>
    )
}