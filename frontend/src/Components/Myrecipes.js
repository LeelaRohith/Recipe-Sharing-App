import RecipeReviewCard from "./Recipecard";
import PrimarySearchAppBar from "./Toolbar";
import { TailSpin } from "react-loader-spinner";
import axios from "axios";
import { useState, useEffect, useCallback } from "react";
import Addrecipe from "./Addrecipe";

export default function Myrecipes() {
  const [recipedetails, setRecipedetails] = useState([]);
  const [loading, setLoading] = useState(true);
  const [currentuserid, setCurrentuserid] = useState("");
  const [userfirstname, setUserfirstname] = useState("");
  const [userlastname, setUserlastname] = useState("");
  const [imageData, setImageData] = useState({});

  const fetchData = useCallback(async () => {
    const headers = {
      Authorization: "Bearer " + localStorage.getItem("token"),
    };
    const res = await axios.get(
      `${process.env.React_App_Backend_Url}/api/v1/user`,
      {
        headers,
      }
    );

    //console.log(response.data);

    setCurrentuserid(res.data.id);
    setUserfirstname(res.data.firstname);
    setUserlastname(res.data.lastname);

    const recipeResponse = await axios.get(
      `${process.env.React_App_Backend_Url}/api/v1/user/currentuserrecipes/` +
        res.data.id,
      {
        headers,
      }
    );
    setLoading(false);

    setRecipedetails(recipeResponse.data);
    recipeResponse.data.map(async (item, index) => {
      const image = item.image;

      setImageData((prev) => ({ ...prev, [image]: image }));
    });
  }, []);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  return (
    <div>
      <PrimarySearchAppBar></PrimarySearchAppBar>
      <br></br>
      <div style={{ textAlign: "center" }}>
        <Addrecipe
          recipedetails={recipedetails}
          setRecipedetails={setRecipedetails}
          setImageData={setImageData}
        ></Addrecipe>
      </div>
      <br></br>
      {!loading ? null : (
        <div
          style={{
            display: "flex",
            flexDirection: "column",
            justifyContent: "center",
            alignItems: "center",
          }}
        >
          <TailSpin
            visible={loading}
            height="30"
            width="30"
            color="#1976D2"
            ariaLabel="tail-spin-loading"
            radius="1"
            wrapperStyle={{}}
            wrapperClass=""
          />
          <div>Loading recipes</div>
        </div>
      )}

      <div
        style={{
          display: "flex",
          flexDirection: "row",
          alignItems: "center",
          justifyContent: "center",
          flexWrap: "wrap",
          gap: "20px",
        }}
      >
        {imageData &&
          recipedetails &&
          recipedetails.map((item, index) => {
            return (
              <div key={index}>
                <RecipeReviewCard
                  recipedetails={recipedetails}
                  setRecipedetails={setRecipedetails}
                  details={item}
                  userfirstname={userfirstname}
                  userlastname={userlastname}
                  image={imageData[item.image]}
                  type="edit"
                  userid={currentuserid}
                ></RecipeReviewCard>
              </div>
            );
          })}
      </div>
    </div>
  );
}
